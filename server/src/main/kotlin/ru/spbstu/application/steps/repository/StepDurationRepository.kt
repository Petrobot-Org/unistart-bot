package ru.spbstu.application.steps.repository

import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.StepDuration
import java.time.Duration

interface StepDurationRepository {
    fun get(step: Step): StepDuration?
    fun changeDuration(step: Step, duration: Duration)
}
