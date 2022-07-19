package ru.spbstu.application.steps.entities

import ru.spbstu.application.auth.entities.User

data class UserWithCompletedSteps(
    val user: User,
    val completedSteps: List<CompletedStep>
)
