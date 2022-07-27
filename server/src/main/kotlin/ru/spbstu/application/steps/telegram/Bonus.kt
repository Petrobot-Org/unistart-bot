package ru.spbstu.application.steps.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.UserId
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.notifications.NextStepNotifier
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.usecases.CheckAndUpdateBonusAccountingUseCase
import ru.spbstu.application.telegram.Strings
import java.time.Instant

private val checkAndUpdateBonusAccounting: CheckAndUpdateBonusAccountingUseCase by GlobalContext.get().inject()
private val nextStepNotifier: NextStepNotifier by GlobalContext.get().inject()

suspend fun TelegramBot.giveBonusWithMessage(
    userId: UserId,
    bonusType: BonusType,
    step: Step
) {
    val result = checkAndUpdateBonusAccounting(User.Id(userId.chatId), bonusType, step, Instant.now())
    if (result.stageBonus != null) {
        sendTextMessage(userId, Strings.NewBonusForStage(result.stageBonus))
    }
    if (result.stepBonus != null) {
        sendTextMessage(userId, Strings.NewBonusForStep(result.stepBonus, step))
        nextStepNotifier.rescheduleFor(User.Id(userId.chatId))
    }
}
