package ru.spbstu.application.auth.usecases

import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransaction
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.CompletedStepRepository
import java.time.Instant

class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val completedStepRepository: CompletedStepRepository,
    private val transaction: DatabaseTransaction
) {
    operator fun invoke(
        userId: User.Id,
        phoneNumber: PhoneNumber,
        avatar: Avatar,
        occupation: Occupation,
        startStep: Long,
        at: Instant
    ) = transaction {
        userRepository.add(User(userId, phoneNumber, avatar, occupation, startStep, 0))
        (0 until startStep).forEach {
            completedStepRepository.add(Step(it), userId, at)
        }
    }
}
