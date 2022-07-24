package ru.spbstu.application.notifications

import dev.inmo.tgbotapi.bot.TelegramBot
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.CompletedStepRepository
import java.time.Instant
import java.time.Period
import java.time.ZoneId
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.time.Duration.Companion.days

class Notifier(
    private val completedStepRepository: CompletedStepRepository,
    private val zoneId: ZoneId
) {
    fun start(sendMessage: (User.Id, Step) -> Unit) {
        val threshold = Period.ofDays(2)
        val period = 1.days
        Timer().scheduleAtFixedRate(time = Date.from(Instant.now()), period.inWholeMilliseconds) {
            val now = Instant.now().atZone(zoneId).toLocalDate()
            val users = completedStepRepository.getUsersWithCompletedSteps()
            users.forEach { (user, completedSteps) ->
                val lastCompletedStep = completedSteps.maxBy { it.step.value }
                val completionDate = lastCompletedStep.endTime.atZone(zoneId).toLocalDate()
                if (completionDate + threshold == now && lastCompletedStep.step.value != 4L) {
                    sendMessage(user.id, lastCompletedStep.step)
                }
            }
        }
    }
}
