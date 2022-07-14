package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.spbstu.application.auth.telegram.handleStart
import ru.spbstu.application.auth.telegram.onSubscriberCommand
import ru.spbstu.application.auth.telegram.onSubscriberText
import ru.spbstu.application.steps.telegram.handleStats
import ru.spbstu.application.steps.telegram.handleStep1
import ru.spbstu.application.steps.telegram.handleSteps
import ru.spbstu.application.trendyfriendy.sendTrendyFriendyApp

class TelegramBot(token: TelegramToken) {
    val bot = telegramBot(token.value)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun start() {
        coroutineScope.launch {
            bot.buildBehaviourWithLongPolling(
                defaultExceptionsHandler = { it.printStackTrace() }
            ) {
                onCommand("start") { handleStart(it) }
                onSubscriberCommand("steps") { handleSteps(it) }
                onSubscriberCommand("stats") { handleStats(it) }
                onSubscriberText(Strings.Step1, Strings.BackToIdeaGeneration) { handleStep1(it) }
                onSubscriberText(Strings.GetMyStats) { handleStats(it) }
                onSubscriberText(Strings.BackToSteps) { handleSteps(it) }
                onSubscriberText(Strings.TrendyFriendy) { sendTrendyFriendyApp(it.chat) }
                onSubscriberText(*Strings.IdeaGenerationWithDescription.keys.toTypedArray()) {
                    sendTextMessage(it.chat.id, Strings.IdeaGenerationWithDescription.getValue(it.content.text))
                }
            }.join()
        }
    }
}

@JvmInline
value class TelegramToken(val value: String)
