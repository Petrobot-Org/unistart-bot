package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.spbstu.application.auth.telegram.handleStart
import ru.spbstu.application.steps.telegram.steps

class TelegramBot(token: TelegramToken) {
    private val bot = telegramBot(token.value)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun start() {
        coroutineScope.launch {
            bot.buildBehaviourWithLongPolling {
                onCommand("start", scenarioReceiver = { handleStart(it) })
                onCommand("steps", scenarioReceiver = { steps(it) })
            }.join()
        }
    }
}

@JvmInline
value class TelegramToken(val value: String)
