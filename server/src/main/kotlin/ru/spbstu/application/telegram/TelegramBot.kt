package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.spbstu.application.auth.telegram.handleStart
import ru.spbstu.application.steps.telegram.handleStats
import ru.spbstu.application.steps.telegram.handleStep1
import ru.spbstu.application.steps.telegram.handleSteps

class TelegramBot(token: TelegramToken) {
    private val bot = telegramBot(token.value)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun start() {
        coroutineScope.launch {
            bot.buildBehaviourWithLongPolling {
                onCommand("start", scenarioReceiver = { handleStart(it) })
                onCommand("steps", scenarioReceiver = { handleSteps(it) })
                onCommand("stats", scenarioReceiver = { handleStats(it) })
                onText({it.content.text == Strings.Step1}){ handleStep1(it)}
                onText({it.content.text == Strings.GetMyStats}){ handleStats(it)}
                onText({it.content.text ==  Strings.BackToSteps}){ handleSteps(it)}
            }.join()
        }
    }
}

@JvmInline
value class TelegramToken(val value: String)
