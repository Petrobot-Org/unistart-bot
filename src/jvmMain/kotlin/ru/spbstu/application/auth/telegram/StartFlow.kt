package ru.spbstu.application.auth.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
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
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.telegram.Strings.Avatars
import ru.spbstu.application.telegram.Strings.HaveAlreadyHadIdea
import ru.spbstu.application.telegram.Strings.InvalidAvatar
import ru.spbstu.application.telegram.Strings.InvalidOccupation
import ru.spbstu.application.telegram.Strings.NoIdea
import ru.spbstu.application.telegram.Strings.NotMyIdea
import ru.spbstu.application.telegram.Strings.Occupations
import ru.spbstu.application.telegram.Strings.SoSoIdea
import ru.spbstu.application.telegram.Strings.StartWithFirstStep
import ru.spbstu.application.telegram.Strings.StartWithSecondStep
import ru.spbstu.application.telegram.Strings.Student
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
    var hasOccupation = false
    var hasIdea = false

//    if (userRepository.contains(phoneNumber)) {

    ///послать 3 картинки с аватарами и подписями

    val avatar = waitText(
        SendTextMessage(
            message.chat.id, Strings.ChooseAvatar,
            replyMarkup = ReplyKeyboardMarkup(
                buttons = Avatars.keys.map { SimpleKeyboardButton(it) }.toTypedArray(),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).map { Avatars[it.text] }
        .onEach { if (it == null) sendTextMessage(message.chat.id, InvalidAvatar) }
        .firstNotNull()

    val keyboard = replyKeyboard(
        resizeKeyboard = true,
        oneTimeKeyboard = true
    )
    {
        row {
            simpleButton(Occupations.keys.elementAt(0))
            simpleButton(Occupations.keys.elementAt(1))
        }
        row {
            simpleButton(Occupations.keys.elementAt(2))
            simpleButton(Occupations.keys.elementAt(3))
        }
        row {
            simpleButton(Occupations.keys.elementAt(4))
            simpleButton(Occupations.keys.elementAt(5))
        }
    }

    var oc = Occupation.values()[0]

    while (!hasOccupation) {
        var occupation = waitText(
            SendTextMessage(
                message.chat.id, Strings.ChooseOccupation,
                replyMarkup = ReplyKeyboardMarkup(
                    buttons = arrayOf(
                        SimpleKeyboardButton(Occupations.keys.elementAt(6)),
                        SimpleKeyboardButton(Occupations.keys.elementAt(7)),
                        SimpleKeyboardButton(Student)
                    ),
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                )
            )
        ).first().text


        if (occupation == Student) {
            occupation = waitText(
                SendTextMessage(
                    message.chat.id, Strings.ChooseCourse,
                    replyMarkup = keyboard
                )
            ).first().text
        }

        if (Occupations.contains(occupation)) {
            oc = Occupations.getValue(occupation)
            hasOccupation = true
        } else {
            sendTextMessage(message.chat.id, InvalidOccupation)
        }
    }

    val keyboardl = replyKeyboard(
        resizeKeyboard = true,
        oneTimeKeyboard = true
    )
    {
        row { simpleButton(SuperIdea) }
        row { simpleButton(NotMyIdea) }
        row { simpleButton(NoIdea) }
        row { simpleButton(SoSoIdea) }
    }
    var startLevel = 0
    var firstStepInfo = ""

    while (!hasIdea) {
        val level = waitText(
            SendTextMessage(
                message.chat.id, HaveAlreadyHadIdea,
                replyMarkup = keyboardl
            )
        ).first().text

        if ((level == SuperIdea) || (level == NotMyIdea)) {
            startLevel = 2
            firstStepInfo = StartWithSecondStep
            hasIdea = true
        } else if ((level == SoSoIdea) || (level == NoIdea)) {
            startLevel = 1
            firstStepInfo = StartWithFirstStep
            hasIdea = true
        }
    }
    sendTextMessage(message.chat.id, firstStepInfo)

    val user = User(User.Id(message.chat.id.chatId), phoneNumber, avatar, oc, startLevel)
    userRepository.add(user)
//    } else {
//        sendTextMessage(message.chat.id, Strings.NoPhoneInDataBase)
//    }
}
