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
private val buttons = listOf<String>(
    Strings.Bisociation,
    Strings.DelphiBrainstormMethod,
    Strings.SCAMPER,
    Strings.TrendyFriendy,
    Strings.Back
)

suspend fun BehaviourContext.handleStep1(message: CommonMessage<TextContent>) {
    val buttons = buttons.map { SimpleKeyboardButton(it) }
    val stage = waitText(
        SendTextMessage(
            message.chat.id, Strings.Variants,
            replyMarkup = ReplyKeyboardMarkup(
                *buttons.toTypedArray(),
                resizeKeyboard = true,
                oneTimeKeyboard = false
            )
        )
    ).first().text
//     тут switch case нужен будет!
    if (stage.equals(Strings.Back)) {
        ideas(message)
    }
}

suspend fun BehaviourContext.ideas(message: CommonMessage<TextContent>) {
    val user = userRepository.get(User.Id(message.chat.id.chatId))
    //todo :
    // иначе не проверим пока что закомиченно
//    if (user != null) {
//          val buttons = steps.take(user.availableStepsCount).map { SimpleKeyboardButton(it) }
    val selectedStep = waitText(
        SendTextMessage(
            message.chat.id, Strings.Ideas,
            replyMarkup = ReplyKeyboardMarkup(
//                    *buttons.toTypedArray(),
                SimpleKeyboardButton(Strings.Step1),
                SimpleKeyboardButton(Strings.Step2),
                SimpleKeyboardButton(Strings.Step3),
                SimpleKeyboardButton(Strings.Step4),
                resizeKeyboard = true,
                oneTimeKeyboard = false
            )
        )
    ).first().text
    when (selectedStep) {
        Strings.Step1 -> handleStep1(message)
        // TODO: 30.06.2022
        /*
         * В БУДУЩЕМ ДОБАВИТЕ !
        */
//        Strings.Step2 -> handleStep2()
//        Strings.Step3 -> handleStep3()
//        Strings.Step4 -> handleStep4()
    }
}
//}


