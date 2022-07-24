package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.spbstu.application.admin.telegram.adminCommands
import ru.spbstu.application.auth.telegram.handleStart
import ru.spbstu.application.auth.telegram.onSubscriberCommand
import ru.spbstu.application.auth.telegram.onSubscriberText
import ru.spbstu.application.notifications.Notifier
import ru.spbstu.application.steps.telegram.handleIdeaGenerationMethods
import ru.spbstu.application.steps.telegram.handleStats
import ru.spbstu.application.steps.telegram.handleStep1
import ru.spbstu.application.steps.telegram.handleSteps

class TelegramBot(token: TelegramToken, private val notifier: Notifier) {
    val bot = telegramBot(token.value)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun start() {
        coroutineScope.launch {
            bot.buildBehaviourWithLongPolling(
                defaultExceptionsHandler = { it.printStackTrace() }
            ) {
                provideHelp {
                    onCommandWithHelp("start", Strings.Help.Start) { handleStart(it) }
                    onSubscriberCommand("steps", Strings.Help.Steps) { handleSteps(it) }
                    onSubscriberCommand("stats", Strings.Help.Stats) { handleStats(it) }
                    onSubscriberText(Strings.Step1, IdeaGenerationStrings.BackToIdeaGeneration) { handleStep1(it) }
                    onSubscriberText(Strings.GetMyStats) { handleStats(it) }
                    onSubscriberText(IdeaGenerationStrings.BackToSteps) { handleSteps(it) }
                    onSubscriberText(*IdeaGenerationStrings.IdeaGenerationWithDescription.keys.toTypedArray()) { handleIdeaGenerationMethods(it) }
                    adminCommands()
                }
                notifier.start { userId, step ->
                    coroutineScope.launch {
                        sendTextMessage(userId.value.toChatId(), "Ты прошёл шаг ${step.value}. Пора приниматься за следующий!")
                    }
                }
            }.join()
        }
    }
}

@Serializable
@JvmInline
value class TelegramToken(val value: String)
