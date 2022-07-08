package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Step
import ru.spbstu.application.data.source.AppDatabase
import java.time.Duration
import java.time.Instant

class StepRepositoryImpl(private val database: AppDatabase) : StepRepository {
    private val step = { id: Step.Id, start: Instant, duration: Duration ->
        Step(id, start, duration)
    }

    override fun get(id: Step.Id): Step {
        return database.stepQueries.getStepById(id, step).executeAsOne()
    }


    override fun changeDuration(id: Step.Id, duration: Duration): Step {
        return Step(get(id).id, get(id).start, duration)
    }
}