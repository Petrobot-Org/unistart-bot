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
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.usecases.IsAdminUseCase
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.StartInfoRepository
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.auth.usecases.RegisterAdminUserUseCase
import ru.spbstu.application.auth.usecases.RegisterUserUseCase
import ru.spbstu.application.notifications.NextStepNotifier
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
import ru.spbstu.application.telegram.sendPhotoResource
import ru.spbstu.application.telegram.waitContactFrom
import ru.spbstu.application.telegram.waitTextFrom
import java.time.Instant

private val isAdmin: IsAdminUseCase by GlobalContext.get().inject()
private val userRepository: UserRepository by GlobalContext.get().inject()
private val startInfoRepository: StartInfoRepository by GlobalContext.get().inject()
private val registerUser: RegisterUserUseCase by GlobalContext.get().inject()
private val registerAdminUser: RegisterAdminUserUseCase by GlobalContext.get().inject()
private val nextStepNotifier: NextStepNotifier by GlobalContext.get().inject()

suspend fun BehaviourContext.handleStart(message: CommonMessage<TextContent>) {
    val userId = User.Id(message.chat.id.chatId)

    if (userRepository.contains(userId)) {
        sendTextMessage(message.chat.id, UserHasAlreadyBeenRegistered)
        handleSteps(message)
        return
    }
    val phoneNumber = waitPhoneNumber(message.chat)

    if (userRepository.contains(phoneNumber)) {
        sendTextMessage(message.chat.id, PhoneNumberIsAlreadyInDatabase)
        return
    }

    if (isAdmin(userId)) {
        try {
            registerAdminUser(userId, phoneNumber, Instant.now())
        } catch (e: Exception) {
            sendTextMessage(message.chat, Strings.DatabaseError)
            throw e
        }
        handleSteps(message)
        return
    }

    if (!startInfoRepository.contains(phoneNumber)) {
        sendTextMessage(message.chat.id, Strings.NoPhoneInDatabase)
        return
    }

    val avatar = waitAvatar(message.chat)
    val occupation = waitOccupation(message.chat)
    val (startLevel, firstStepInfo) = waitStartLevel(message.chat)

    try {
        registerUser(userId, phoneNumber, avatar, occupation, startLevel, Instant.now())
        nextStepNotifier.rescheduleFor(userId)
    } catch (e: Exception) {
        sendTextMessage(message.chat, Strings.DatabaseError)
        throw e
    }

    sendTextMessage(message.chat.id, firstStepInfo)
    handleSteps(message)
}

private suspend fun BehaviourContext.waitStartLevel(chat: Chat) =
    waitTextFrom(
        chat,
        SendTextMessage(
            chat.id, HaveIdeaQuestion,
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

private suspend fun BehaviourContext.waitOccupation(chat: Chat) =
    waitTextFrom(
        chat,
        SendTextMessage(
            chat.id, Strings.ChooseOccupation,
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
                chat.id, Strings.ChooseCourse,
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
            sendTextMessage(chat.id, InvalidOccupation)
        }
    }.map { OccupationByString[it.text] }
        .firstNotNull()

private suspend fun BehaviourContext.waitAvatar(chat: Chat): Avatar {
    bot.sendPhotoResource(
        chat = chat,
        resourcePath = Strings.StartAvatars, Strings.ChooseAvatar,
        replyMarkup = ReplyKeyboardMarkup(
            buttons = AvatarByString.keys.map { SimpleKeyboardButton(it) }.toTypedArray(),
            resizeKeyboard = true,
            oneTimeKeyboard = true
        )
    )

    return waitTextFrom(chat)
        .map { AvatarByString[it.text] }
        .onEach { if (it == null) sendTextMessage(chat.id, InvalidAvatar) }
        .firstNotNull()
}

private suspend fun BehaviourContext.waitPhoneNumber(chat: Chat) =
    waitContactFrom(
        chat,
        SendTextMessage(
            chat.id, Strings.WelcomeRequirePhone,
            replyMarkup = ReplyKeyboardMarkup(
                RequestContactKeyboardButton(Strings.SendPhoneButton),
                resizeKeyboard = true,
                oneTimeKeyboard = true
            )
        )
    ).map { PhoneNumber.valueOf(it.contact.phoneNumber)!! }.first()
