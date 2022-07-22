package ru.spbstu.application.auth.usecases

import ru.spbstu.application.admin.usecases.IsAdminUseCase
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.StartInfoRepository
import ru.spbstu.application.auth.repository.SubscriptionRepository
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransaction
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.CompletedStepRepository
import java.time.Instant

class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val completedStepRepository: CompletedStepRepository,
    private val transaction: DatabaseTransaction,
    private val subscriptionRepository: SubscriptionRepository,
    private val startInfoRepository: StartInfoRepository,
    private val isAdmin: IsAdminUseCase
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
        if (!isAdmin(userId)) {
            val startInfo = startInfoRepository.getByPhoneNumber(phoneNumber)!!
            subscriptionRepository.add(startInfo.begin, startInfo.duration, userId)
        }
    }
}
