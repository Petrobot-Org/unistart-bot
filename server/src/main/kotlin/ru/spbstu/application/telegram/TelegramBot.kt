package ru.spbstu.application.telegram

import com.ithersta.tgbotapi.fsm.entities.StateMachine
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.bot.settings.limiters.CommonLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.serialization.Serializable
import ru.spbstu.application.notifications.ConfigureNotifiers

class TelegramBot(
    token: TelegramToken,
    private val configureNotifiers: ConfigureNotifiers,
    private val stateMachine: StateMachine<*, *, *>
) {
    val bot = telegramBot(token.value) {
        requestsLimiter = CommonLimiter(lockCount = 30, regenTime = 1000)
        client = HttpClient(OkHttp)
    }

    suspend fun start() {
        bot.buildBehaviourWithLongPolling(
            defaultExceptionsHandler = { it.printStackTrace() }
        ) {
            configureNotifiers(scope)
            with(stateMachine) { collect() }
        }.join()
    }
}

@Serializable
@JvmInline
value class TelegramToken(val value: String)
