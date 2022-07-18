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
            BonusType.BrainstormMethod,
            BonusType.TrendyFriendy
        )
    )

) {
    operator fun invoke(
        userId: User.Id,
        bonusType: BonusType,
        step: Step,
        at: Instant
    ) = transaction {
        val user = userRepository.get(userId)///выбрасывать исключение, если null?
        var oldAmountOfCoins = user!!.amountOfCoins
        var valueOfBonusType = 0

        if (bonusAccountingRepository.get(userId, bonusType) == null)//этап пройден в первый раз
        {
            valueOfBonusType = 1
            if (bonusType == BonusType.TrendyFriendy) valueOfBonusType = 5
            user.amountOfCoins = oldAmountOfCoins + valueOfBonusType
            bonusAccountingRepository.add(BonusAccounting(userId = userId, bonusType = bonusType))
        }
        if (stepsWithBonusType[step] == bonusAccountingRepository.getByUsedId(userId))//шаг полностью пройден
        {
            oldAmountOfCoins = user.amountOfCoins
            val stepStartedAt = completedStepRepository.getCompletedStepsByUser(user).last().endTime
            val durationOfStep = Duration.between(stepStartedAt, at)
            if (durationOfStep < Duration.ofDays(14))
            {
                valueOfBonusType = 3
            }
            if (durationOfStep < Duration.ofDays(7))
            {
                valueOfBonusType = 5
            }
            if (durationOfStep < Duration.ofHours(72)){
                valueOfBonusType = 8
            }
            user.amountOfCoins = oldAmountOfCoins + valueOfBonusType
            completedStepRepository.add(step, userId, at)
            val newAvailableStepsCount = user.availableStepsCount + 1
            user.availableStepsCount=newAvailableStepsCount
        }
    }
}