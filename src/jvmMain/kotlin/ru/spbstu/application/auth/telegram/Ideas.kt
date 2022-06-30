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
private var steps = listOf<String>(Strings.Step1, Strings.Step2, Strings.Step3, Strings.Step4)
private var buttons = listOf<String>(Strings.Button1, Strings.Button2, Strings.Button3, Strings.Button4, Strings.BBack)

suspend fun BehaviourContext.handleStep1(message: CommonMessage<TextContent>) {
   val s =  waitText(
        SendTextMessage(
            message.chat.id, Strings.WelcomeRequirePhone,
            replyMarkup = ReplyKeyboardMarkup(
//                *buttons.toTypedArray(),
                SimpleKeyboardButton(Strings.Button1),
                SimpleKeyboardButton(Strings.Button2),
                SimpleKeyboardButton(Strings.Button3),
                SimpleKeyboardButton(Strings.Button4),
                SimpleKeyboardButton(Strings.BBack),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).first().text
    if (s.equals(Strings.BBack)){
        Ideas(message)
    }
}

suspend fun BehaviourContext.Ideas(message: CommonMessage<TextContent>) {
    var user = userRepository.get(User.Id(message.chat.id.chatId))
    // Тут проверка пока что закомментирован потому что нет нормального USERa
//    if (user != null) {
//        var buttons = steps.take(user.availableStepsCount).map { SimpleKeyboardButton(it) }
    val selectedStep = waitText(
        SendTextMessage(
            message.chat.id, Strings.Ideas,
            replyMarkup = ReplyKeyboardMarkup(
//                    *steps.toTypedArray(),
                SimpleKeyboardButton(Strings.Step1),
                SimpleKeyboardButton(Strings.Step2),
                SimpleKeyboardButton(Strings.Step3),
                SimpleKeyboardButton(Strings.Step4),
                resizeKeyboard = true,
                oneTimeKeyboard = true
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


