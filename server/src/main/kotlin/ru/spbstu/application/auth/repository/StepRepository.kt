package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.StepTimeFrame
import java.time.Duration

interface StepRepository {
    fun get(id: StepTimeFrame.Id): StepTimeFrame
    fun changeDuration(id: StepTimeFrame.Id, duration: Duration): StepTimeFrame
}
