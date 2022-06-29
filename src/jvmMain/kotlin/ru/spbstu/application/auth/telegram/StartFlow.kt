package ru.spbstu.application.auth.telegram

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitContact
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
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
    ).first()

    var av = Avatar.values()[0];
    for (i in 0..2) {
        if (avatar.toString() == Avatars[i]) {
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
    ).first().toString()

//    if (occupation == Occupations[8]) {
    var occ = waitText(
        SendTextMessage(
            message.chat.id, Strings.ChooseCourse,
            replyMarkup = ReplyKeyboardMarkup(
                buttons = arrayOf(
                    SimpleKeyboardButton(Occupations[0]),
                    SimpleKeyboardButton(Occupations[1]),
                    SimpleKeyboardButton(Occupations[2]),
                    SimpleKeyboardButton(Occupations[3]),
                    SimpleKeyboardButton(Occupations[4]),
                    SimpleKeyboardButton(Occupations[5])
                ),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).first().toString()
//    }

    var oc = Occupation.values()[0];
    for (i in 0..8) {
        if (occupation == Occupations[i]) {
            oc = Occupation.values()[i]
        }
    }

    val level = waitText(
        SendTextMessage(
            message.chat.id, Strings.HaveAlreadyHadIdea,
            replyMarkup = ReplyKeyboardMarkup(
                buttons = arrayOf(
                    SimpleKeyboardButton(SuperIdea),
                    SimpleKeyboardButton(NotMyIdea),
                    SimpleKeyboardButton(NoIdea),
                    SimpleKeyboardButton(SoSoIdea)
                ),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).first()

    var startLevel = 0
    var firstStepInfo = ""
    for (i in 0..8) {
        if ((level.toString() == SuperIdea) || (level.toString() == NotMyIdea)) {
            startLevel = 2
            firstStepInfo = StartWithSecondStep
        } else {
            startLevel = 1
            firstStepInfo = StartWithFirstStep
        }
    }

    SendTextMessage(message.chat.id, firstStepInfo)

    val user = User(User.Id(message.chat.id.chatId), phoneNumber, av, oc, startLevel)
    userRepository.add(user)
//    else
//    {
//        SendTextMessage(message.chat.id, Strings.NoPhoneInDataBase)
//    }
}
