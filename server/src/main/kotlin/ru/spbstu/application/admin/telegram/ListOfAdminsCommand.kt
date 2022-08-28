@file:OptIn(PreviewFeature::class)

package ru.spbstu.application.admin.telegram

import com.ithersta.tgbotapi.fsm.entities.triggers.*
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.*
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.DocumentContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.PreviewFeature
import org.koin.core.context.GlobalContext
import ru.spbstu.application.AppConfig
import ru.spbstu.application.admin.usecases.AddAdminsUseCase
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.entities.users.AdminUser
import ru.spbstu.application.auth.entities.users.RootAdminUser
import ru.spbstu.application.auth.repository.AdminRepository
import ru.spbstu.application.telegram.StateMachineBuilder
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.entities.state.AdminMenu
import ru.spbstu.application.telegram.entities.state.WaitingForAdminToAdd
import ru.spbstu.application.telegram.entities.state.WaitingForDeleteConfirmation

private val adminRepository: AdminRepository by GlobalContext.get().inject()
private val appConfig: AppConfig by GlobalContext.get().inject()
private val addAdmins: AddAdminsUseCase by GlobalContext.get().inject()

@OptIn(PreviewFeature::class)
fun StateMachineBuilder.listOfAdminsCommand() {
    role<AdminUser> {
        state<AdminMenu> {
            onText(Strings.AdminPanel.Menu.ListOfAdmins) {
                sendTextMessage(
                    it.chat,
                    text = Strings.AdminPanel.ListOfAdmins.Header,
                    replyMarkup = listOfAdminButtons()
                )
            }
        }
    }
    role<RootAdminUser> {
        state<AdminMenu> {
            onDataCallbackQuery(Regex("delete admin:\\d+"), handler = {
                val userId = it.data.split(":")[1].toLong()
                setState(
                    WaitingForDeleteConfirmation(
                        User.Id(userId),
                        it.messageCallbackQueryOrThrow().message.messageId
                    )
                )
            })
            onDataCallbackQuery(Regex("delete root admin"), handler = {
                sendTextMessage(chat = it.from, text = Strings.AdminPanel.ListOfAdmins.CantDeleteRootAdmin)
            })
            onDataCallbackQuery(Regex("add admin"), handler = {
                setState(WaitingForAdminToAdd(it.messageCallbackQueryOrThrow().message.messageId))
            })
        }
        state<WaitingForAdminToAdd> {
            onTransition {
                sendTextMessage(it, Strings.AdminPanel.ListOfAdmins.ChooseTheWayOfAddition)
            }
            onContact {
                val userId = it.content.contact.userId ?: run {
                    sendTextMessage(it.chat, Strings.AdminPanel.ListOfAdmins.ErrorNoTelegram)
                    return@onContact
                }
                adminRepository.add(User.Id(userId.chatId))
                editMessageReplyMarkup(
                    chat = it.chat,
                    messageId = state.messageId,
                    replyMarkup = listOfAdminButtons()
                )
                setState(AdminMenu)
            }
            onDocument { message ->
                val phoneNumbers = getPhoneNumbersFromXlsx(message)
                if (phoneNumbers.isEmpty()) {
                    return@onDocument
                }
                runCatching {
                    val failedNumbers = addAdmins(phoneNumbers.toSet())
                    if (failedNumbers.isNotEmpty()) {
                        sendTextMessage(message.chat, Strings.AdminPanel.ListOfAdmins.UnableToAddAdmin(failedNumbers))
                    }
                }.onFailure {
                    sendTextMessage(message.chat, Strings.DatabaseError)
                }
                editMessageReplyMarkup(
                    chat = message.chat,
                    messageId = state.messageId,
                    replyMarkup = listOfAdminButtons()
                )
                setState(AdminMenu)
            }
        }
        state<WaitingForDeleteConfirmation> {
            onTransition {
                sendTextMessage(
                    it,
                    text = Strings.AdminPanel.ListOfAdmins
                        .ConfirmationOfDeletion(getChat(UserId(state.userId.value)).asPrivateChat()!!),
                    replyMarkup = replyKeyboard(
                        resizeKeyboard = true
                    ) {
                        row {
                            simpleButton(Strings.AdminPanel.ListOfAdmins.Yes)
                            simpleButton(Strings.AdminPanel.ListOfAdmins.No)
                        }
                    }
                )
            }
            onText(Strings.AdminPanel.ListOfAdmins.Yes, Strings.AdminPanel.ListOfAdmins.No) { message ->
                val answer = message.content.text == Strings.AdminPanel.ListOfAdmins.Yes
                if (answer) {
                    adminRepository.delete(state.userId)
                    editMessageReplyMarkup(
                        chat = message.chat,
                        messageId = state.messageId,
                        replyMarkup = listOfAdminButtons()
                    )
                }
                setState(AdminMenu)
            }
        }
    }
    role<AdminUser> {
        state<AdminMenu> {
            onDataCallbackQuery(Regex("delete admin:\\d+"), handler = {
                sendTextMessage(it.from, Strings.UnauthorizedError)
            })
            onDataCallbackQuery(Regex("delete root admin"), handler = {
                sendTextMessage(it.from, Strings.UnauthorizedError)
            })
            onDataCallbackQuery(Regex("add admin"), handler = {
                sendTextMessage(it.from, Strings.UnauthorizedError)
            })
        }
    }
}

private suspend fun RequestsExecutor.listOfAdminButtons(): InlineKeyboardMarkup {
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
