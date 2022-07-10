package ru.spbstu.application.steps.repository

import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.StepTimeFrame
import java.time.Duration

interface StepTimeFrameRepository {
    fun get(step: Step): StepTimeFrame?
    fun changeDuration(step: Step, duration: Duration)
}
