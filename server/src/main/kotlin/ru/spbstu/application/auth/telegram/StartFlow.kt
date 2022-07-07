package ru.spbstu.application.auth.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.steps.telegram.steps
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.Avatars
import ru.spbstu.application.telegram.Strings.HaveIdeaQuestion
import ru.spbstu.application.telegram.Strings.InvalidAvatar
import ru.spbstu.application.telegram.Strings.InvalidOccupation
import ru.spbstu.application.telegram.Strings.NoIdea
import ru.spbstu.application.telegram.Strings.NotMyIdea
import ru.spbstu.application.telegram.Strings.Occupations
import ru.spbstu.application.telegram.Strings.PhoneNumberIsAlreadyInDataBase
import ru.spbstu.application.telegram.Strings.SoSoIdea
import ru.spbstu.application.telegram.Strings.StartWithFirstStep
import ru.spbstu.application.telegram.Strings.StartWithSecondStep
import ru.spbstu.application.telegram.Strings.Student
import ru.spbstu.application.telegram.Strings.SuperIdea
import ru.spbstu.application.telegram.waitContactFrom
import ru.spbstu.application.telegram.waitTextFrom

private val userRepository: UserRepository by GlobalContext.get().inject()

suspend fun BehaviourContext.handleStart(message: CommonMessage<TextContent>) {
    val phoneNumber = waitContactFrom(
        message.chat,
        SendTextMessage(
            message.chat.id, Strings.WelcomeRequirePhone,
            replyMarkup = ReplyKeyboardMarkup(
                RequestContactKeyboardButton(Strings.SendPhoneButton),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).map { PhoneNumber(it.contact.phoneNumber) }.first()
    if (userRepository.contains(phoneNumber)) { ////TODO: добавить так же проверку на наличие номера в базе номеров от Оксаны
        sendTextMessage(message.chat.id, PhoneNumberIsAlreadyInDataBase)
        return
    }
    // проверить номер телефона
    // else sendTextMessage(message.chat.id, Strings.NoPhoneInDataBase)
    // послать 3 картинки с аватарами и подписями

    val avatar = waitTextFrom(
        message.chat,
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

    val occupation = waitTextFrom(
        message.chat,
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
    ).onEach {
        if (it.text == Student) {
            sendTextMessage(
                message.chat.id, Strings.ChooseCourse,
                replyMarkup = replyKeyboard(
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
            )
        } else if (it.text !in Occupations) {
            sendTextMessage(message.chat.id, InvalidOccupation)
        }
    }.map { Occupations[it.text] }
        .firstNotNull()

    val (startLevel, firstStepInfo) = waitTextFrom(
        message.chat,
        SendTextMessage(
            message.chat.id, HaveIdeaQuestion,
            replyMarkup = replyKeyboard(
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
            {
                row { simpleButton(SuperIdea) }
                row { simpleButton(NotMyIdea) }
                row { simpleButton(NoIdea) }
                row { simpleButton(SoSoIdea) }
            }
        )
    ).map {
        when (it.text) {
            SuperIdea, NotMyIdea -> 2L to StartWithSecondStep
            SoSoIdea, NoIdea -> 1L to StartWithFirstStep
            else -> null
        }
    }.firstNotNull()

    sendTextMessage(message.chat.id, firstStepInfo)


    val user = User(User.Id(message.chat.id.chatId), phoneNumber, avatar, occupation, startLevel, 0)
    userRepository.add(user)

    steps(message)
}
