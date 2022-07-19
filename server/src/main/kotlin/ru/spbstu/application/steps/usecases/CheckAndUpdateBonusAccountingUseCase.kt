package ru.spbstu.application.steps.usecases

import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.ChatIdentifier
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransaction
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.BonusAccountingRepository
import ru.spbstu.application.steps.repository.CompletedStepRepository
import ru.spbstu.application.telegram.Strings.NewBonusForStage
import ru.spbstu.application.telegram.Strings.NewBonusForStep
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
    private val transaction: DatabaseTransaction
) {
    operator fun invoke(
        chatId: ChatIdentifier, userId: User.Id, bonusType: BonusType, step: Step, at: Instant
    ) = transaction {
        val user = userRepository.get(userId)
        val stageBonus = bonusTypeWithBonusValue[bonusType]!!

        if (bonusAccountingRepository.get(userId, bonusType) == null) {
            userRepository.setAmountOfCoins(userId, user!!.amountOfCoins + stageBonus)
            SendTextMessage(chatId, NewBonusForStage(stageBonus))
        }
        if (bonusAccountingRepository.getBonusesByUsedId(userId).containsAll(stepsWithBonusType[step]!!)) {
            if (completedStepRepository.get(userId, step) != null) {
                return@transaction
            }
            val stepStartedAt = completedStepRepository.getCompletedStepsByUser(user!!).last().endTime
            val durationOfStep = Duration.between(stepStartedAt, at)
            val stepBonus = when {
                durationOfStep < Duration.ofHours(72) -> 8L
                durationOfStep < Duration.ofDays(7) -> 5L
                durationOfStep < Duration.ofDays(14) -> 3L
                else -> 0L
            }
            userRepository.setAmountOfCoins(
                userId,
                user.amountOfCoins + stageBonus + stepBonus
            )
            completedStepRepository.add(step, userId, at)
            userRepository.setAvailableStepsCount(userId, user.availableStepsCount + 1)
            SendTextMessage(chatId, NewBonusForStep(stepBonus, step))
        }
    }
}
