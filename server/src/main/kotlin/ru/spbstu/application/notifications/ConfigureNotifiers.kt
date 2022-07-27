package ru.spbstu.application.notifications

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.spbstu.application.telegram.Strings

class ConfigureNotifiers(
    private val nextStepNotifier: NextStepNotifier
) {
    context(TelegramBot)
    operator fun invoke(scope: CoroutineScope) {
        nextStepNotifier.start { userId ->
            scope.launch {
                sendTextMessage(userId.value.toChatId(), "Strings.Notifications.NextStep(nextStep)")
            }
        }
    }
}
