package ru.spbstu.application.steps.entities

import ru.spbstu.application.auth.entities.User
import java.time.Instant

class CompletedStep(
    val userId: User.Id,
    val step: Step,
    val endTime: Instant)