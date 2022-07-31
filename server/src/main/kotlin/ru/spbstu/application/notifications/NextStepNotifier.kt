package ru.spbstu.application.notifications

import org.quartz.JobBuilder.newJob
import org.quartz.JobExecutionContext
import org.quartz.JobKey
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import ru.spbstu.application.AppConfig
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.extensions.times
import ru.spbstu.application.steps.entities.CompletedStep
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.CompletedStepRepository
import ru.spbstu.application.steps.usecases.GetStepDurationUseCase
import java.time.Duration
import java.time.Instant
import java.util.*

private const val NextStepGroup = "nextStep"
private const val UserIdKey = "userId"
private const val StepKey = "step"
private const val BonusKey = "bonus"

typealias SendNextStepMessage = (User.Id, Duration, Step, Long) -> Unit

class NextStepNotifier(
    private val config: AppConfig,
    private val completedStepRepository: CompletedStepRepository,
    private val getStepDuration: GetStepDurationUseCase
) {
    private val scheduler = StdSchedulerFactory().scheduler

    fun start(sendMessage: SendNextStepMessage) {
        scheduler.setJobFactory { _, _ -> Job(sendMessage, config.notifications.nextStep.before) }
        rescheduleAll()
        scheduler.start()
    }

    fun shutdown() {
        scheduler.shutdown()
    }

    fun rescheduleFor(userId: User.Id) {
        val completedSteps = completedStepRepository.getByUserId(userId)
        rescheduleFor(userId, completedSteps)
    }

    private fun rescheduleFor(userId: User.Id, completedSteps: List<CompletedStep>) {
        val jobKey = JobKey.jobKey(userId.value.toString(), NextStepGroup)
        scheduler.deleteJob(jobKey)

        val nextStep = completedSteps.maxOf { it.step.value } + 1
        if (nextStep > Step.LastValue) {
            return
        }
        val endTime = completedSteps.maxOf { it.endTime }
        val stepDuration = getStepDuration(Step(nextStep)).duration

        val job = newJob(Job::class.java)
            .withIdentity(jobKey)
            .usingJobData(UserIdKey, userId.value)
            .usingJobData(StepKey, nextStep)
            .build()

        val triggers = config.durationToBonus.map {
            val duration = stepDuration * it.durationFactor
            val deadline = endTime + duration
            val softDeadline = deadline - config.notifications.nextStep.before
            if (softDeadline.isBefore(Instant.now())) {
                return@map null
            }
            newTrigger()
                .startAt(Date.from(softDeadline))
                .usingJobData(BonusKey, it.bonus)
                .build()
        }.filterNotNull().toSet()

        scheduler.scheduleJob(job, triggers, true)
    }

    private fun rescheduleAll() {
        scheduler.clear()
        completedStepRepository.getUsersWithCompletedSteps().forEach {
            rescheduleFor(it.user.id, it.completedSteps)
        }
    }

    class Job(
        private val sendMessage: SendNextStepMessage,
        private val duration: Duration
    ) : org.quartz.Job {
        override fun execute(context: JobExecutionContext?) {
            val jobDataMap = context!!.jobDetail.jobDataMap
            val triggerDataMap = context.trigger.jobDataMap
            val userId = User.Id(jobDataMap.getLong(UserIdKey))
            val step = Step(jobDataMap.getLong(StepKey))
            val bonus = triggerDataMap.getLong(BonusKey)
            sendMessage(userId, duration, step, bonus)
        }
    }
}
