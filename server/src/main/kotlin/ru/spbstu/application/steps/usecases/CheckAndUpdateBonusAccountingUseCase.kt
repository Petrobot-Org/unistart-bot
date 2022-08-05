package ru.spbstu.application.steps.usecases

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransactionWithResult
import ru.spbstu.application.steps.entities.BonusAccounting
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.BonusAccountingRepository
import ru.spbstu.application.steps.repository.CompletedStepRepository
import java.lang.Long.max
import java.time.Instant

private val stepsWithBonusType: Map<Step, List<BonusType>> = mapOf(
    Step(1) to listOf(
        BonusType.Bisociation,
        BonusType.DelphiMethod,
        BonusType.BrainstormMethod,
        BonusType.TrendyFriendy,
        BonusType.ScamperS,
        BonusType.ScamperC,
        BonusType.ScamperA,
        BonusType.ScamperM,
        BonusType.ScamperP,
        BonusType.ScamperE,
        BonusType.ScamperR
    )
)

private val bonusTypeWithBonusValue: Map<BonusType, Long> = mapOf(
    BonusType.Bisociation to 1L,
    BonusType.DelphiMethod to 1L,
    BonusType.BrainstormMethod to 1L,
    BonusType.TrendyFriendy to 5L,
    BonusType.ScamperS to 1L,
    BonusType.ScamperC to 1L,
    BonusType.ScamperA to 1L,
    BonusType.ScamperM to 1L,
    BonusType.ScamperP to 1L,
    BonusType.ScamperE to 1L,
    BonusType.ScamperR to 1L
)

class CheckAndUpdateBonusAccountingUseCase(
    private val userRepository: UserRepository,
    private val bonusAccountingRepository: BonusAccountingRepository,
    private val completedStepRepository: CompletedStepRepository,
    private val calculateDurationBonus: CalculateDurationBonusUseCase,
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
            bonusAccountingRepository.getBonusesByUserId(userId).containsAll(stepsWithBonusType[step]!!) &&
            completedStepRepository.get(userId, step) == null
        ) {
            completedStepRepository.add(step, userId, at)
            userRepository.setAvailableStepsCount(userId, max(user.availableStepsCount, step.value + 1))

            val stepStartedAt = completedStepRepository.getByUserId(user.id).maxOf { it.endTime }
            calculateDurationBonus(step, stepStartedAt, at)
        } else {
            null
        }

        userRepository.setAmountOfCoins(userId, user.amountOfCoins + (stageBonus ?: 0) + (stepBonus ?: 0))

        Result(stageBonus = stageBonus, stepBonus = stepBonus)
    }
}
