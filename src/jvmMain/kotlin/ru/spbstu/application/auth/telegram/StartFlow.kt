package ru.spbstu.application.auth.telegram

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitContact
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.telegram.Strings.Avatars
import ru.spbstu.application.telegram.Strings.NoIdea
import ru.spbstu.application.telegram.Strings.NotMyIdea
import ru.spbstu.application.telegram.Strings.Occupations
import ru.spbstu.application.telegram.Strings.SoSoIdea
import ru.spbstu.application.telegram.Strings.StartWithFirstStep
import ru.spbstu.application.telegram.Strings.StartWithSecondStep
import ru.spbstu.application.telegram.Strings.SuperIdea

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

//    if (userRepository.contains(phoneNumber)) {
        ///послать 3 картинки с аватарами и подписями
        val avatar = waitText(
            SendTextMessage(
                message.chat.id, Strings.ChooseAvatar,
                replyMarkup = ReplyKeyboardMarkup(
                    buttons = arrayOf(
                        SimpleKeyboardButton(Avatars[0]),
                        SimpleKeyboardButton(Avatars[1]),
                        SimpleKeyboardButton(Avatars[2])
                    ),
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                )
            )
        ).first().text

        var av = Avatar.values()[0];
        for (i in 0..2) {
            if (avatar == Avatars[i]) {
                av = Avatar.values()[i]
            }
        }

        var occupation = waitText(
            SendTextMessage(
                message.chat.id, Strings.ChooseOccupation,
                replyMarkup = ReplyKeyboardMarkup(
                    buttons = arrayOf(
                        SimpleKeyboardButton(Occupations[6]), SimpleKeyboardButton(Occupations[7]),
                        SimpleKeyboardButton(Occupations[8])
                    ),
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                )
            )
        ).first().text

        val keyboard = replyKeyboard(
            resizeKeyboard = true,
            oneTimeKeyboard = true
        )
        {
            row{simpleButton(Occupations[0])
                simpleButton(Occupations[1])}
            row{simpleButton(Occupations[2])
                simpleButton(Occupations[3])}
            row{simpleButton(Occupations[4])
                simpleButton(Occupations[5])}
        }

        if (occupation == Occupations[8]) {
            occupation = waitText(
                SendTextMessage(
                    message.chat.id, Strings.ChooseCourse,
                    replyMarkup = keyboard
                )
            ).first().text
        }

        var oc = Occupation.values()[0];
        for (i in 0..8) {
            if (occupation == Occupations[i]) {
                oc = Occupation.values()[i]
            }
        }

        val keyboardl = replyKeyboard(
            resizeKeyboard = true,
            oneTimeKeyboard = true
        )
        {
            row{simpleButton(SuperIdea)}
            row{simpleButton(NotMyIdea)}
            row{simpleButton(NoIdea)}
            row{simpleButton(SoSoIdea)}
        }
        val level = waitText(
            SendTextMessage(
                message.chat.id, Strings.HaveAlreadyHadIdea,
                replyMarkup = keyboardl
            )
        ).first().text

        var startLevel = 0
        var firstStepInfo = ""

        if ((level.toString() == SuperIdea) || (level.toString() == NotMyIdea)) {
            startLevel = 2
            firstStepInfo = StartWithSecondStep
        } else {
            startLevel = 1
            firstStepInfo = StartWithFirstStep
        }

        sendTextMessage(message.chat.id, firstStepInfo)

        val user = User(User.Id(message.chat.id.chatId), phoneNumber, av, oc, startLevel)
        userRepository.add(user)
//    } else {
//        sendTextMessage(message.chat.id, Strings.NoPhoneInDataBase)
//    }
}
