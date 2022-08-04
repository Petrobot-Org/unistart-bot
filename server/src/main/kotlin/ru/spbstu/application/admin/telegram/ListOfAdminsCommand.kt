@file:OptIn(PreviewFeature::class)

package ru.spbstu.application.admin.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.*
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.DocumentContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.context.GlobalContext
import ru.spbstu.application.AppConfig
import ru.spbstu.application.admin.usecases.AddAdminsUseCase
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.AdminRepository
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.waitContactFrom
import ru.spbstu.application.telegram.waitTextFrom

private val adminRepository: AdminRepository by GlobalContext.get().inject()
private val appConfig: AppConfig by GlobalContext.get().inject()
private val addAdmins: AddAdminsUseCase by GlobalContext.get().inject()

suspend fun BehaviourContext.listOfAdminsCommand() {
    onAdminText(Strings.AdminPanel.Menu.ListOfAdmins) { uploadListOfAdmins(it.chat) }
    onRootAdminDataCallbackQuery(Regex("delete admin:\\d+")) { deleteAdmin(it) }
    onRootAdminDataCallbackQuery(Regex("delete root admin")) { deleteRootAdmin(it.from) }
    onRootAdminDataCallbackQuery(Regex("add admin")) {
        addAdmin(it)
    }
    onAdminDocument(initialFilter = { it.content.media.fileName?.equals("admins.xlsx") == true }) {
        onAdminsUploaded(it)
    }
}

private suspend fun BehaviourContext.deleteRootAdmin(chat: Chat) {
    send(chat = chat, text = Strings.AdminPanel.ListOfAdmins.CantDeleteRootAdmin)
}

private suspend fun BehaviourContext.uploadListOfAdmins(chat: Chat) {
    sendTextMessage(
        chat = chat,
        text = Strings.AdminPanel.ListOfAdmins.Header,
        replyMarkup = listOfAdminButtons()
    ).messageId
}

private suspend fun BehaviourContext.addAdmin(dataCallbackQuery: DataCallbackQuery) {
    val wayOfAddition = waitTextFrom(
        dataCallbackQuery.from,
        SendTextMessage(dataCallbackQuery.from.id, Strings.AdminPanel.ListOfAdmins.ChooseTheWayOfAddition,
            replyMarkup = replyKeyboard(
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
            {
                row {
                    simpleButton(Strings.AdminPanel.ListOfAdmins.AddByContact)
                    simpleButton(Strings.AdminPanel.ListOfAdmins.AddByXlsxTable)
                }
            })
    ).first { it.text == Strings.AdminPanel.ListOfAdmins.AddByContact || it.text == Strings.AdminPanel.ListOfAdmins.AddByXlsxTable }
        .text
    if (wayOfAddition == Strings.AdminPanel.ListOfAdmins.AddByContact) {
        sendTextMessage(dataCallbackQuery.from, Strings.AdminPanel.ListOfAdmins.SendContact)
        val userId = waitContactFrom(dataCallbackQuery.from).map {
            it.contact.userId
        }.first() ?: run {
            sendTextMessage(dataCallbackQuery.from, Strings.AdminPanel.ListOfAdmins.ErrorNoTelegram)
            return
        }
        adminRepository.add(User.Id(userId.chatId))
    } else {
        sendTextMessage(dataCallbackQuery.from, Strings.AdminPanel.ListOfAdmins.FormatOfXlsxTable)
    }
    editMessageReplyMarkup(
        chat = dataCallbackQuery.from,
        messageId = dataCallbackQuery.messageCallbackQueryOrThrow().message.messageId,
        replyMarkup = listOfAdminButtons()
    )
}

private suspend fun BehaviourContext.onAdminsUploaded(message: CommonMessage<DocumentContent>) {
    val phoneNumbers = getPhoneNumbersFromXlsx(message)
    if (phoneNumbers.isEmpty()) {
        return
    }
    try {
        val failedNumbers = addAdmins(phoneNumbers.toSet())
        sendTextMessage(message.chat, Strings.AdminPanel.ListOfAdmins.UnableToAddAdmin(failedNumbers))
    } catch (e: Exception) {
        sendTextMessage(message.chat, Strings.DatabaseError)
        throw e
    }
}

private suspend fun BehaviourContext.listOfAdminButtons(): InlineKeyboardMarkup {
    return inlineKeyboard {
        appConfig.rootAdminUserIds.forEach {
            val chat = getChat(ChatId(it.value)).asPrivateChat()!!
            row {
                dataButton(
                    Strings.AdminPanel.ListOfAdmins.NameOfRootAdmin(chat),
                    "delete root admin"
                )
            }
        }
        adminRepository.findAll().forEach { admin ->
            val chat = getChat(ChatId(admin.id.value)).asPrivateChat()!!
            row {
                dataButton(
                    Strings.AdminPanel.ListOfAdmins.NameOfAdmin(chat),
                    "delete admin:${admin.id.value}"
                )
            }
        }
        row {
            dataButton(Strings.AdminPanel.ListOfAdmins.AddAdmin, "add admin")
        }
    }
}

private suspend fun BehaviourContext.deleteAdmin(dataCallbackQuery: DataCallbackQuery) {
    val userId = dataCallbackQuery.data.split(":")[1].toLong()

    sendTextMessage(
        chat = dataCallbackQuery.from,
        text = Strings.AdminPanel.ListOfAdmins.ConfirmationOfDeletion(getChat(UserId(userId)).asPrivateChat()!!),
        replyMarkup = replyKeyboard(
            resizeKeyboard = true
        ) {
            row {
                simpleButton(Strings.AdminPanel.ListOfAdmins.Yes)
                simpleButton(Strings.AdminPanel.ListOfAdmins.No)
            }
        }
    ).messageId
    val answer = waitTextFrom(dataCallbackQuery.from).map {
        when (it.text) {
            Strings.AdminPanel.ListOfAdmins.Yes -> true
            Strings.AdminPanel.ListOfAdmins.No -> false
            else -> null
        }
    }.firstNotNull()
    if (answer) {
        adminRepository.delete(User.Id(userId))
        editMessageReplyMarkup(
            chat = dataCallbackQuery.from,
            messageId = dataCallbackQuery.messageCallbackQueryOrThrow().message.messageId,
            replyMarkup = listOfAdminButtons()
        )
    }
    sendTextMessage(
        chat = dataCallbackQuery.from,
        text = Strings.AdminPanel.Header,
        replyMarkup = createAdminPanel()
    )
}
