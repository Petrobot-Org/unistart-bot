package ru.spbstu.application.steps.entities

import java.time.Duration
import java.time.Instant

data class StepTimeFrame(
    val step: Step,
    val start: Instant,
    val duration: Duration
)
