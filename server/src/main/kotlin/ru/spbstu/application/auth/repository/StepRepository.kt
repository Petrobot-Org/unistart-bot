package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Step
import java.time.Duration

interface StepRepository {
    fun get(id: Step.Id): Step
    fun changeDuration(id: Step.Id, duration: Duration): Step
}