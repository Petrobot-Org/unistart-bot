package ru.spbstu.application.auth.telegram

import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.fsm.entities.triggers.onContact
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import org.koin.core.component.inject
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.entities.users.EmptyUser
import ru.spbstu.application.auth.repository.StartInfoRepository
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.auth.usecases.IsAdminUseCase
import ru.spbstu.application.auth.usecases.RegisterAdminUserUseCase
import ru.spbstu.application.auth.usecases.RegisterUserUseCase
import ru.spbstu.application.notifications.NextStepNotifier
import ru.spbstu.application.telegram.StateMachineBuilder
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.entities.state.EmptyState
import ru.spbstu.application.telegram.entities.state.StartStates
import ru.spbstu.application.telegram.sendPhotoResource
import java.time.Instant

fun StateMachineBuilder.startFlow() {
    val isAdmin: IsAdminUseCase by inject()
    val userRepository: UserRepository by inject()
    val startInfoRepository: StartInfoRepository by inject()
    val registerUser: RegisterUserUseCase by inject()
    val registerAdminUser: RegisterAdminUserUseCase by inject()
    val nextStepNotifier: NextStepNotifier by inject()
    role<EmptyUser> {
        state<EmptyState> {
            onCommand("start", Strings.Help.Start) {
                setState(StartStates.WaitingForContact)
            }
        }
        state<StartStates.WaitingForContact> {
            onTransition {
                sendTextMessage(
                    it, Strings.WelcomeRequirePhone,
                    replyMarkup = ReplyKeyboardMarkup(
                        RequestContactKeyboardButton(Strings.SendPhoneButton),
                        resizeKeyboard = true,
                        oneTimeKeyboard = true
                    )
                )
            }
            onContact { message ->
                val phoneNumber = PhoneNumber.valueOf(message.content.contact.phoneNumber.filter { it.isDigit() })!!
                if (userRepository.contains(phoneNumber)) {
                    sendTextMessage(message.chat.id, Strings.PhoneNumberIsAlreadyInDatabase)
                    setState(EmptyState)
                    return@onContact
                }
                val userId = User.Id(message.chat.id.chatId)
                if (isAdmin(userId)) {
                    runCatching {
                        registerAdminUser(userId, phoneNumber, Instant.now())
                        refreshCommands()
                    }.onFailure {
                        sendTextMessage(message.chat, Strings.DatabaseError)
                    }
                    setState(EmptyState)
                    return@onContact
                }
                if (!startInfoRepository.contains(phoneNumber)) {
                    sendTextMessage(message.chat.id, Strings.NoPhoneInDatabase)
                    setState(EmptyState)
                    return@onContact
                }
                setState(StartStates.WaitingForAvatar(phoneNumber))
            }
        }
        state<StartStates.WaitingForAvatar> {
            onTransition {
                sendPhotoResource(
                    chat = it,
                    resourcePath = Strings.StartAvatars, Strings.ChooseAvatar,
                    replyMarkup = ReplyKeyboardMarkup(
                        buttons = Strings.AvatarByString.keys.map { SimpleKeyboardButton(it) }.toTypedArray(),
                        resizeKeyboard = true,
                        oneTimeKeyboard = true
                    )
                )
            }
            onText {
                val avatar = Strings.AvatarByString[it.content.text] ?: run {
                    sendTextMessage(it.chat, Strings.InvalidAvatar)
                    return@onText
                }
                setState(StartStates.WaitingForOccupation(state.phoneNumber, avatar))
            }
        }
        state<StartStates.WaitingForOccupation> {
            onTransition {
                sendTextMessage(
                    it, Strings.ChooseOccupation,
                    replyMarkup = ReplyKeyboardMarkup(
                        buttons = arrayOf(
                            SimpleKeyboardButton(Strings.OccupationByString.keys.elementAt(6)),
                            SimpleKeyboardButton(Strings.OccupationByString.keys.elementAt(7)),
                            SimpleKeyboardButton(Strings.Student)
                        ),
                        resizeKeyboard = true,
                        oneTimeKeyboard = true
                    )
                )
            }
            onText {
                Strings.OccupationByString[it.content.text]?.let { occupation ->
                    setState(StartStates.WaitingForStartLevel(state.phoneNumber, state.avatar, occupation))
                    return@onText
                }
                if (it.content.text == Strings.Student) {
                    sendTextMessage(
                        it.chat, Strings.ChooseCourse,
                        replyMarkup = replyKeyboard(
                            resizeKeyboard = true,
                            oneTimeKeyboard = true
                        ) {
                            Strings.OccupationByString.keys.take(6).chunked(2).forEach {
                                row {
                                    it.forEach { simpleButton(it) }
                                }
                            }
                        }
                    )
                } else {
                    sendTextMessage(it.chat, Strings.InvalidOccupation)
                }
            }
        }
        state<StartStates.WaitingForStartLevel> {
            onTransition {
                sendTextMessage(
                    it, Strings.HaveIdeaQuestion,
                    replyMarkup = replyKeyboard(
                        resizeKeyboard = true,
                        oneTimeKeyboard = true
                    ) {
                        row { simpleButton(Strings.SuperIdea) }
                        row { simpleButton(Strings.NotMyIdea) }
                        row { simpleButton(Strings.NoIdea) }
                        row { simpleButton(Strings.SoSoIdea) }
                    }
                )
            }
            onText { message ->
                val (startLevel, firstStepInfo) = when (message.content.text) {
                    Strings.SuperIdea, Strings.NotMyIdea -> 2L to Strings.StartWithSecondStep
                    Strings.SoSoIdea, Strings.NoIdea -> 1L to Strings.StartWithFirstStep
                    else -> return@onText
                }
                val userId = User.Id(message.chat.id.chatId)
                runCatching {
                    registerUser(userId, state.phoneNumber, state.avatar, state.occupation, startLevel, Instant.now())
                    nextStepNotifier.rescheduleFor(userId)
                    sendTextMessage(message.chat.id, firstStepInfo)
                    refreshCommands()
                }.onFailure {
                    sendTextMessage(message.chat, Strings.DatabaseError)
                }
                setState(EmptyState)
            }
        }
    }
}
