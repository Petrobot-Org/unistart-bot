package ru.spbstu.application.admin.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.GlobalContext
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.StepDurationRepository
import ru.spbstu.application.steps.usecases.GetStepDurationUseCase
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.AdminPanel.StepDuration
import ru.spbstu.application.telegram.waitTextFrom
import java.time.Duration

private val getStepDuration: GetStepDurationUseCase by GlobalContext.get().inject()
private val stepDurationRepository: StepDurationRepository by GlobalContext.get().inject()

suspend fun BehaviourContext.stepDurationCommand() {
    onAdminText(Strings.AdminPanel.Menu.StepDuration) { showStepDurations(it) }
    onAdminDataCallbackQuery(Regex("change_step_duration \\d")) { changeStepDuration(it) }
}

private suspend fun BehaviourContext.showStepDurations(message: CommonMessage<TextContent>) {
    sendTextMessage(message.chat, StepDuration.Header, replyMarkup = stepDurationKeyboard())
}

@OptIn(PreviewFeature::class)
private suspend fun BehaviourContext.changeStepDuration(dataCallbackQuery: DataCallbackQuery) {
    val step = Step(dataCallbackQuery.data.split(' ')[1].toLong())
    val duration = waitTextFrom(
        dataCallbackQuery.from,
        SendTextMessage(dataCallbackQuery.from.id, StepDuration.Change(step))
    )
        .map {
            try {
                val days = it.text.toLong()
                require(days > 0)
                Duration.ofDays(days)
            } catch (e: Exception) {
                null
            }
        }
        .onEach { if (it == null) sendTextMessage(dataCallbackQuery.from, Strings.AdminPanel.InvalidDurationDays) }
        .firstNotNull()

    stepDurationRepository.changeDuration(step, duration)

    editMessageReplyMarkup(
        chat = dataCallbackQuery.from,
        messageId = dataCallbackQuery.messageCallbackQueryOrThrow().message.messageId,
        replyMarkup = stepDurationKeyboard()
    )
}

private fun stepDurationKeyboard(): InlineKeyboardMarkup {
    val durations = (1L..4L).map { getStepDuration(Step(it)) }
    return inlineKeyboard {
        durations.forEach {
            row {
                dataButton(StepDuration.Button(it), "change_step_duration ${it.step.value}")
            }
        }
    }
}
