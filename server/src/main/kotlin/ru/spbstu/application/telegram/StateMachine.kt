package ru.spbstu.application.telegram

import com.ithersta.tgbotapi.fsm.StatefulContext
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.builders.StateFilterBuilder
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.builders.stateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onCommand
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import io.ktor.util.logging.*
import mu.KotlinLogging
import ru.spbstu.application.admin.telegram.adminCommands
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.entities.users.BaseUser
import ru.spbstu.application.auth.entities.users.ExpiredUser
import ru.spbstu.application.auth.entities.users.SubscribedUser
import ru.spbstu.application.auth.telegram.startFlow
import ru.spbstu.application.auth.usecases.GetUserUseCase
import ru.spbstu.application.steps.telegram.handleStats
import ru.spbstu.application.steps.telegram.sendAvailableSteps
import ru.spbstu.application.steps.telegram.step1
import ru.spbstu.application.telegram.commands.cancelCommand
import ru.spbstu.application.telegram.commands.fallback
import ru.spbstu.application.telegram.commands.stateCommand
import ru.spbstu.application.telegram.entities.state.DialogState
import ru.spbstu.application.telegram.entities.state.EmptyState
import ru.spbstu.application.telegram.repository.UserDialogStateRepository
import java.time.Instant

typealias StateMachineBuilder = StateMachineBuilder<DialogState, BaseUser, UserId>
typealias RoleFilterBuilder<U> = RoleFilterBuilder<DialogState, BaseUser, U, UserId>
typealias StateFilterBuilder<S, U> = StateFilterBuilder<DialogState, BaseUser, S, U, UserId>
typealias StatefulContext<S, U> = StatefulContext<DialogState, BaseUser, S, U>

private val logger = KotlinLogging.logger { }

fun createStateMachine(
    getUser: GetUserUseCase,
    stateRepository: UserDialogStateRepository
) = stateMachine({ getUser(User.Id(it.chatId), Instant.now()) }, stateRepository) {
    onException { userId, throwable ->
        logger.error(throwable)
        sendTextMessage(userId, Strings.Exception(throwable::class.toString()))
    }
    includeHelp()
    anyRole {
        anyState {
            cancelCommand()
            stateCommand()
        }
    }
    startFlow()
    adminCommands()
    role<SubscribedUser> {
        state<EmptyState> {
            onTransition { sendAvailableSteps(it, user) }
            onCommand("stats", Strings.Help.Stats) { handleStats(it) }
            onText(Strings.GetMyStats) { handleStats(it) }
        }
        step1()
    }
    expiredUserFallback()
    fallback()
}

private fun StateMachineBuilder<DialogState, BaseUser, UserId>.expiredUserFallback() {
    role<ExpiredUser> {
        anyState {
            onText {
                sendTextMessage(it.chat, Strings.NotSubscribed)
            }
        }
    }
}
