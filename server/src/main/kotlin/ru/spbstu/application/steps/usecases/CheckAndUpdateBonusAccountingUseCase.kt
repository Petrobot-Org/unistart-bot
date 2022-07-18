package ru.spbstu.application.steps.usecases

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransaction
import ru.spbstu.application.steps.entities.BonusAccounting
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.BonusAccountingRepository
import ru.spbstu.application.steps.repository.CompletedStepRepository
import java.time.Duration
import java.time.Instant

class CheckAndUpdateBonusAccountingUseCase(
    private val userRepository: UserRepository,
    private val bonusAccountingRepository: BonusAccountingRepository,
    private val completedStepRepository: CompletedStepRepository,
    private val transaction: DatabaseTransaction,
    private val stepsWithBonusType: Map<Step, List<BonusType>> = mapOf(
        Step(1) to listOf(
            BonusType.Bisociation,
            BonusType.DelphiMethod,
            BonusType.BrainstormMethod,
            BonusType.Scamper,
            BonusType.TrendyFriendy
        )
    ),
    private val bonusTypeWithBonusValue: Map<BonusType, Long> = mapOf(
        BonusType.Bisociation to 1L,
        BonusType.DelphiMethod to 1L,
        BonusType.BrainstormMethod to 1L,
        BonusType.Scamper to 1L,
        BonusType.TrendyFriendy to 5L
    )
) {
    operator fun invoke(
        userId: User.Id, bonusType: BonusType, step: Step, at: Instant
    ) = transaction {
        val user = userRepository.get(userId)///выбрасывать исключение, если null?

        if (bonusAccountingRepository.get(userId, bonusType) == null)//этап пройден в первый раз
        {
            userRepository.setAmountOfCoins(userId, user!!.amountOfCoins + bonusTypeWithBonusValue[bonusType]!!)
            bonusAccountingRepository.add(BonusAccounting(userId = userId, bonusType = bonusType))
        }
        val allBonuses = hashSetOf(stepsWithBonusType[step])
        val earnedBonuses = hashSetOf(bonusAccountingRepository.getByUsedId(userId))
        if (allBonuses == earnedBonuses)//шаг полностью пройден
        {
            if (completedStepRepository.get(userId, step) != null) {
                return@transaction
            }
            val stepStartedAt = completedStepRepository.getCompletedStepsByUser(user!!).last().endTime
            val durationOfStep = Duration.between(stepStartedAt, at)
            val valueOfBonusType = when {
                durationOfStep < Duration.ofHours(72) -> 8
                durationOfStep < Duration.ofDays(7) -> 5
                durationOfStep < Duration.ofDays(14) -> 3
                else -> 0
            }
            userRepository.setAmountOfCoins(userId, user.amountOfCoins + valueOfBonusType)
            completedStepRepository.add(step, userId, at)
            userRepository.setAvailableStepsCount(userId, user.availableStepsCount + 1)
        }
    }
}