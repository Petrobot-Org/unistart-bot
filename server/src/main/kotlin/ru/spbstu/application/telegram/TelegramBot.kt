package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.bot.settings.limiters.CommonLimiter
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import ru.spbstu.application.admin.telegram.adminCommands
import ru.spbstu.application.auth.telegram.handleStart
import ru.spbstu.application.auth.telegram.onSubscriberCommand
import ru.spbstu.application.auth.telegram.onSubscriberText
import ru.spbstu.application.notifications.ConfigureNotifiers
import ru.spbstu.application.steps.telegram.handleIdeaGenerationMethods
import ru.spbstu.application.steps.telegram.handleStats
import ru.spbstu.application.steps.telegram.handleStep1
import ru.spbstu.application.steps.telegram.handleSteps

class TelegramBot(token: TelegramToken, private val configureNotifiers: ConfigureNotifiers) {
    val bot = telegramBot(token.value) {
        requestsLimiter = CommonLimiter(20, 1000)
    }

    suspend fun start() {
        bot.buildBehaviourWithLongPolling(
            defaultExceptionsHandler = { it.printStackTrace() }
        ) {
            provideHelp {
                onCommandWithHelp("start", Strings.Help.Start) { handleStart(it) }
                onSubscriberCommand("steps", Strings.Help.Steps) { handleSteps(it) }
                onSubscriberCommand("stats", Strings.Help.Stats) { handleStats(it) }
                adminCommands()
            }
            onSubscriberText(Strings.Step1, IdeaGenerationStrings.BackToIdeaGeneration) { handleStep1(it) }
            onSubscriberText(Strings.GetMyStats) { handleStats(it) }
            onSubscriberText(IdeaGenerationStrings.BackToSteps) { handleSteps(it) }
            onSubscriberText(*IdeaGenerationStrings.IdeaGenerationWithDescription.keys.toTypedArray()) {
                handleIdeaGenerationMethods(it)
            }
            configureNotifiers(scope)
        }.join()
    }
}

@Serializable
@JvmInline
value class TelegramToken(val value: String)
