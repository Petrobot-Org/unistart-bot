package ru.spbstu.application.steps.repository

import ru.spbstu.application.data.source.AppDatabase
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.StepDuration
import java.time.Duration
import java.time.Instant

class StepDurationRepositoryImpl(private val database: AppDatabase) : StepDurationRepository {
    private val mapper = { step: Step, duration: Duration ->
        StepDuration(step, duration)
    }

    override fun get(step: Step): StepDuration? {
        return database.stepDurationQueries.get(step, mapper).executeAsOneOrNull()
    }

    override fun changeDuration(step: Step, duration: Duration) {
        database.stepDurationQueries.updateDuration(duration, step)
    }
}
