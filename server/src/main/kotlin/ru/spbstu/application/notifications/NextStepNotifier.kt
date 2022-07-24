package ru.spbstu.application.notifications

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.CompletedStepRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.time.Duration.Companion.days

class NextStepNotifier(
    private val completedStepRepository: CompletedStepRepository,
    private val zoneId: ZoneId,
    private val config: NotificationsConfig.NextStep
) {
    fun start(sendMessage: (User.Id, Step) -> Unit) {
        Timer().scheduleAtFixedRate(
            time = Date.from(Instant.from(config.at.atDate(LocalDate.now(zoneId)).atZone(zoneId))),
            period = 1.days.inWholeMilliseconds
        ) {
            val now = Instant.now().atZone(zoneId).toLocalDate()
            val users = completedStepRepository.getUsersWithCompletedSteps()
            users.forEach { (user, completedSteps) ->
                val lastCompletedStep = completedSteps.maxBy { it.step.value }
                val completionDate = lastCompletedStep.endTime.atZone(zoneId).toLocalDate()
                if (lastCompletedStep.step.value != Step.LastValue && completionDate + config.after == now) {
                    val nextStep = Step(lastCompletedStep.step.value + 1)
                    sendMessage(user.id, nextStep)
                }
            }
        }
    }
}
