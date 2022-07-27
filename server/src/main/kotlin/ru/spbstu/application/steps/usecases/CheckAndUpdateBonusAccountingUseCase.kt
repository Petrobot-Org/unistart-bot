package ru.spbstu.application.steps.usecases

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransactionWithResult
import ru.spbstu.application.notifications.NextStepNotifier
import ru.spbstu.application.steps.entities.BonusAccounting
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.BonusAccountingRepository
import ru.spbstu.application.steps.repository.CompletedStepRepository
import java.lang.Long.max
import java.time.Duration
import java.time.Instant

private val stepsWithBonusType: Map<Step, List<BonusType>> = mapOf(
    Step(1) to listOf(
        BonusType.Bisociation,
        BonusType.DelphiMethod,
        BonusType.BrainstormMethod,
        BonusType.Scamper,
        BonusType.TrendyFriendy
    )
)

private val bonusTypeWithBonusValue: Map<BonusType, Long> = mapOf(
    BonusType.Bisociation to 1L,
    BonusType.DelphiMethod to 1L,
    BonusType.BrainstormMethod to 1L,
    BonusType.Scamper to 1L,
    BonusType.TrendyFriendy to 5L
)

class CheckAndUpdateBonusAccountingUseCase(
    private val userRepository: UserRepository,
    private val bonusAccountingRepository: BonusAccountingRepository,
    private val completedStepRepository: CompletedStepRepository,
    private val transactionWithResult: DatabaseTransactionWithResult
) {
    data class Result(
        val stageBonus: Long?,
        val stepBonus: Long?
    )

    operator fun invoke(userId: User.Id, bonusType: BonusType, step: Step, at: Instant) = transactionWithResult {
        val user = userRepository.get(userId)!!

        val stageBonus = if (bonusAccountingRepository.get(userId, bonusType) == null) {
            bonusAccountingRepository.add(BonusAccounting(userId, bonusType))
            bonusTypeWithBonusValue[bonusType]!!
        } else {
            null
        }

        val stepBonus = if (
            bonusAccountingRepository.getBonusesByUsedId(userId).containsAll(stepsWithBonusType[step]!!) &&
            completedStepRepository.get(userId, step) == null
        ) {
            completedStepRepository.add(step, userId, at)
            userRepository.setAvailableStepsCount(userId, max(user.availableStepsCount, step.value + 1))

            val stepStartedAt = completedStepRepository.getByUserId(user.id).maxOf { it.endTime }
            val durationOfStep = Duration.between(stepStartedAt, at)
            when {
                durationOfStep < Duration.ofHours(72) -> 8L
                durationOfStep < Duration.ofDays(7) -> 5L
                durationOfStep < Duration.ofDays(14) -> 3L
                else -> 0L
            }
        } else {
            null
        }

        userRepository.setAmountOfCoins(userId, user.amountOfCoins + (stageBonus ?: 0) + (stepBonus ?: 0))

        Result(stageBonus = stageBonus, stepBonus = stepBonus)
    }
}
