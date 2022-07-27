package ru.spbstu.application.steps.repository

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.steps.entities.CompletedStep
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.UserWithCompletedSteps
import java.time.Instant

interface CompletedStepRepository {
    fun add(step: Step, userId: User.Id, endTime: Instant)
    fun get(userId: User.Id, step: Step): CompletedStep?
    fun getUsersWithCompletedSteps(): List<UserWithCompletedSteps>
    fun getByUserId(userId: User.Id): List<CompletedStep>
}
