package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.StepTimeFrame
import ru.spbstu.application.data.source.AppDatabase
import java.time.Duration
import java.time.Instant

class StepRepositoryImpl(private val database: AppDatabase) : StepRepository {
    private val step = { id: StepTimeFrame.Id, start: Instant, duration: Duration ->
        StepTimeFrame(id, start, duration)
    }

    override fun get(id: StepTimeFrame.Id): StepTimeFrame {
        return database.stepQueries.getStepById(id, step).executeAsOne()
    }


    override fun changeDuration(id: StepTimeFrame.Id, duration: Duration): StepTimeFrame {
        return StepTimeFrame(get(id).id, get(id).start, duration)
    }
}
