package ru.spbstu.application.telegram

import com.ithersta.tgbotapi.fsm.entities.StateMachine
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.bot.settings.limiters.CommonLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import kotlinx.serialization.Serializable
import ru.spbstu.application.admin.telegram.adminCommands
import ru.spbstu.application.notifications.ConfigureNotifiers

class TelegramBot(
    token: TelegramToken,
    private val configureNotifiers: ConfigureNotifiers,
    private val stateMachine: StateMachine<*, *, *>
) {
    val bot = telegramBot(token.value) {
        requestsLimiter = CommonLimiter(20, 1000)
    }

    suspend fun start() {
        bot.buildBehaviourWithLongPolling(
            defaultExceptionsHandler = { it.printStackTrace() }
        ) {
            provideHelp {
                adminCommands()
            }
            configureNotifiers(scope)
            with(stateMachine) { collect() }
        }.join()
    }
}

@Serializable
@JvmInline
value class TelegramToken(val value: String)
