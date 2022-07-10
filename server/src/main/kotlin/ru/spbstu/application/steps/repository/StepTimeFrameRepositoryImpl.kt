package ru.spbstu.application.steps.repository

import ru.spbstu.application.data.source.AppDatabase
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.StepTimeFrame
import java.time.Duration
import java.time.Instant

class StepTimeFrameRepositoryImpl(private val database: AppDatabase) : StepTimeFrameRepository {
    private val mapper = { step: Step, start: Instant, duration: Duration ->
        StepTimeFrame(step, start, duration)
    }

    override fun get(step: Step): StepTimeFrame? {
        return database.stepTimeFrameQueries.get(step, mapper).executeAsOneOrNull()
    }

    override fun changeDuration(step: Step, duration: Duration) {
        database.stepTimeFrameQueries.updateDuration(duration, step)
    }
}
