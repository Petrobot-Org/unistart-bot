package ru.spbstu.application.auth.telegram

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.telegram.Strings

private val userRepository: UserRepository by GlobalContext.get().inject()
private val steps = listOf<String>(Strings.Step1, Strings.Step2, Strings.Step3, Strings.Step4)
private val buttonsForFirstStep = listOf<String>(
    Strings.Bisociation,
    Strings.DelphiBrainstormMethod,
    Strings.Scamper,
    Strings.TrendyFriendy,
    Strings.Back
)

suspend fun BehaviourContext.handleStep1(message: CommonMessage<TextContent>) {
    val buttons = buttonsForFirstStep.map { SimpleKeyboardButton(it) }
    val stage = waitText(
        SendTextMessage(
            message.chat.id, Strings.Variants,
            replyMarkup = ReplyKeyboardMarkup(
                *buttons.toTypedArray(),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).first { it.text in buttonsForFirstStep }.text
    when (stage) {
        Strings.Back -> steps(message)
//        В БУДУЩЕМ ДОБАВИТЕ !
//        Strings.Bisociation -> handleStep2()
//        Strings.DelphiBrainstormMethod -> handleStep2()
//        Strings.SCAMPER -> handleStep3()
//        Strings.TrendyFriendy -> handleStep4()
    }
}

suspend fun BehaviourContext.steps(message: CommonMessage<TextContent>) {
    val user = userRepository.get(User.Id(message.chat.id.chatId))
    if (user != null) {
        val buttons = steps.take(user.availableStepsCount).map { SimpleKeyboardButton(it) }
        val selectedStep = waitText(
            SendTextMessage(
                message.chat.id, Strings.Ideas,
                replyMarkup = ReplyKeyboardMarkup(
                    *buttons.toTypedArray(),
//                    SimpleKeyboardButton(Strings.Step1),
//                    SimpleKeyboardButton(Strings.Step2),
//                    SimpleKeyboardButton(Strings.Step3),
//                    SimpleKeyboardButton(Strings.Step4),
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                )
            )
        ).first { it.text in steps }.text
        when (selectedStep) {
            Strings.Step1 -> handleStep1(message)
//        В БУДУЩЕМ ДОБАВИТЕ !
//        Strings.Step2 -> handleStep2()
//        Strings.Step3 -> handleStep3()
//        Strings.Step4 -> handleStep4()
        }
    }
}