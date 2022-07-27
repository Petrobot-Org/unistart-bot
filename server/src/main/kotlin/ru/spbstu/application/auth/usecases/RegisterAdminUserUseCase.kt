package ru.spbstu.application.auth.usecases

import ru.spbstu.application.admin.usecases.IsAdminUseCase
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransaction
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.CompletedStepRepository
import java.time.Instant

class RegisterAdminUserUseCase(
    private val userRepository: UserRepository,
    private val completedStepRepository: CompletedStepRepository,
    private val transaction: DatabaseTransaction,
    private val isAdmin: IsAdminUseCase
) {
    operator fun invoke(
        userId: User.Id,
        phoneNumber: PhoneNumber,
        at: Instant
    ) = transaction {
        require(isAdmin(userId))
        val user = User(userId, phoneNumber, Avatar.DigitalAgile, Occupation.Businessman, Step.LastValue + 1, 0)
        userRepository.add(user)
        (0..Step.LastValue).forEach {
            completedStepRepository.add(Step(it), userId, at)
        }
    }
}
