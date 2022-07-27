package ru.spbstu.application.notifications

import org.koin.core.context.GlobalContext
import org.quartz.Job
import org.quartz.impl.StdSchedulerFactory
import ru.spbstu.application.auth.entities.User
import java.time.Instant
import java.util.*
import org.quartz.JobBuilder.*
import org.quartz.JobExecutionContext
import org.quartz.TriggerBuilder.*
import org.quartz.TriggerKey

class NextStepNotifier(
    private val config: NotificationsConfig.NextStep
) {
    private val scheduler = StdSchedulerFactory().scheduler

    fun start(sendMessage: (User.Id) -> Unit) {
        scheduler.setJobFactory { _, _ -> Job(sendMessage) }
        scheduler.start()
    }

    fun shutdown() {
        scheduler.shutdown()
    }

    fun scheduleNotification(userId: User.Id) {
        val name = userId.value.toString()
        val group = "nextStep"
        scheduler.unscheduleJob(TriggerKey.triggerKey(name, group))

        val job = newJob(Job::class.java)
            .withIdentity(name, group)
            .usingJobData("userId", userId.value)
            .build()

        val trigger = newTrigger()
            .withIdentity(name, group)
            .startAt(Date.from(Instant.now().plusSeconds(5)))
            .forJob(job)
            .build()

        scheduler.scheduleJob(job, trigger)
    }

    class Job(private val sendMessage: (User.Id) -> Unit) : org.quartz.Job {
        override fun execute(context: JobExecutionContext?) {
            val data = context!!.jobDetail.jobDataMap
            val userId = data.getLong("userId")
            sendMessage(User.Id(userId))
        }
    }
}
