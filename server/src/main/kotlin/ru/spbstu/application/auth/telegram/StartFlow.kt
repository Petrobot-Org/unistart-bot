package ru.spbstu.application.auth.telegram

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitContact
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.telegram.Strings

private val userRepository: UserRepository by GlobalContext.get().inject()

suspend fun BehaviourContext.handleStart(message: CommonMessage<TextContent>) {
    val phoneNumber = waitContact(
        SendTextMessage(
            message.chat.id, Strings.WelcomeRequirePhone,
            replyMarkup = ReplyKeyboardMarkup(
                RequestContactKeyboardButton(Strings.SendPhoneButton),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).map { PhoneNumber(it.contact.phoneNumber) }.first()
    /*
    val avatar = waitText(...)
    ...
    val user = User(User.Id(message.chat.id.chatId), phoneNumber, ...)
    userRepository.add(user)
    ...
    */
}
