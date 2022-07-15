package ru.spbstu.application.auth.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.files.DocumentFile
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.StartInfoRepository
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.auth.usecases.RegisterUserUseCase
import ru.spbstu.application.steps.telegram.handleSteps
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.AvatarByString
import ru.spbstu.application.telegram.Strings.HaveIdeaQuestion
import ru.spbstu.application.telegram.Strings.InvalidAvatar
import ru.spbstu.application.telegram.Strings.InvalidOccupation
import ru.spbstu.application.telegram.Strings.NoIdea
import ru.spbstu.application.telegram.Strings.NotMyIdea
import ru.spbstu.application.telegram.Strings.OccupationByString
import ru.spbstu.application.telegram.Strings.PhoneNumberIsAlreadyInDatabase
import ru.spbstu.application.telegram.Strings.SoSoIdea
import ru.spbstu.application.telegram.Strings.StartWithFirstStep
import ru.spbstu.application.telegram.Strings.StartWithSecondStep
import ru.spbstu.application.telegram.Strings.Student
import ru.spbstu.application.telegram.Strings.SuperIdea
import ru.spbstu.application.telegram.Strings.UserHasAlreadyBeenRegistered
import ru.spbstu.application.telegram.waitContactFrom
import ru.spbstu.application.telegram.waitTextFrom
import java.time.Instant

private val userRepository: UserRepository by GlobalContext.get().inject()
private val startInfoRepository: StartInfoRepository by GlobalContext.get().inject()
private val registerUser: RegisterUserUseCase by GlobalContext.get().inject()

suspend fun BehaviourContext.handleStart(message: CommonMessage<TextContent>) {
    if (userRepository.contains(User.Id(message.chat.id.chatId))) {
        sendTextMessage(message.chat.id, UserHasAlreadyBeenRegistered)
        handleSteps(message)
        return
    }
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
    ).map { PhoneNumber.valueOf(it.contact.phoneNumber)!! }.first()
    if (!startInfoRepository.contains(phoneNumber)) {
        sendTextMessage(message.chat.id, Strings.NoPhoneInDatabase)
        return
    }
    if (userRepository.contains(phoneNumber)) {
        sendTextMessage(message.chat.id, PhoneNumberIsAlreadyInDatabase)
        return
    }
    ///sendDocument (message.chat.id, )
    // послать 3 картинки с аватарами и подписями
    val avatar = waitTextFrom(
        message.chat,
        SendTextMessage(
            message.chat.id, Strings.ChooseAvatar,
            replyMarkup = ReplyKeyboardMarkup(
                buttons = AvatarByString.keys.map { SimpleKeyboardButton(it) }.toTypedArray(),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).map { AvatarByString[it.text] }
        .onEach { if (it == null) sendTextMessage(message.chat.id, InvalidAvatar) }
        .firstNotNull()

    val occupation = waitTextFrom(
        message.chat,
        SendTextMessage(
            message.chat.id, Strings.ChooseOccupation,
            replyMarkup = ReplyKeyboardMarkup(
                buttons = arrayOf(
                    SimpleKeyboardButton(OccupationByString.keys.elementAt(6)),
                    SimpleKeyboardButton(OccupationByString.keys.elementAt(7)),
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
                    OccupationByString.keys.take(6).chunked(2).forEach {
                        row {
                            it.forEach { simpleButton(it) }
                        }
                    }
                }
            )
        } else if (it.text !in OccupationByString) {
            sendTextMessage(message.chat.id, InvalidOccupation)
        }
    }.map { OccupationByString[it.text] }
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

    try {
        registerUser(User.Id(message.chat.id.chatId), phoneNumber, avatar, occupation, startLevel, Instant.now())
    } catch (e: Exception) {
        sendTextMessage(message.chat, Strings.DatabaseError)
        throw e
    }

    sendTextMessage(message.chat.id, firstStepInfo)

    handleSteps(message)
}
