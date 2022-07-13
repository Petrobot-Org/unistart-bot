package ru.spbstu.application.steps.repository

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase
import ru.spbstu.application.steps.entities.CompletedStep
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.UserWithCompletedSteps
import java.time.Instant

class CompletedStepRepositoryImpl(private val database: AppDatabase) : CompletedStepRepository {
    private val mapper = { userId: User.Id,
                           step: Step,
                           endTime: Instant ->
        CompletedStep(userId, step, endTime)
    }

    override fun add(step: Step, userId: User.Id, endTime: Instant) {
        database.completedStepQueries.add(step, userId, endTime)
    }

    override fun getUsersWithCompletedSteps(): List<UserWithCompletedSteps> {
        return database.completedStepQueries.joinUser().executeAsList().groupBy(
            { t -> User(t.id, t.phoneNumber!!, t.avatar, t.occupation, t.availableStepsCount!!, t.amountOfCoins!!) },
            { t -> CompletedStep(t.userId, t.step, t.endTime) })
            .map { t ->
                UserWithCompletedSteps(t.key, t.value)
            }
    }
}
