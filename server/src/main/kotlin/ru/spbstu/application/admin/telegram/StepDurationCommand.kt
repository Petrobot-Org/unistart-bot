package ru.spbstu.application.admin.telegram

import com.ithersta.tgbotapi.fsm.entities.triggers.onDataCallbackQuery
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import org.koin.core.context.GlobalContext
import ru.spbstu.application.auth.entities.users.AdminUser
import ru.spbstu.application.notifications.NextStepNotifier
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.StepDurationRepository
import ru.spbstu.application.steps.usecases.GetStepDurationUseCase
import ru.spbstu.application.telegram.StateMachineBuilder
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.AdminPanel.StepDuration
import ru.spbstu.application.telegram.entities.state.AdminMenu
import ru.spbstu.application.telegram.entities.state.WaitingForStepDuration
import java.time.Duration

private val getStepDuration: GetStepDurationUseCase by GlobalContext.get().inject()
private val stepDurationRepository: StepDurationRepository by GlobalContext.get().inject()
private val nextStepNotifier: NextStepNotifier by GlobalContext.get().inject()

fun StateMachineBuilder.stepDurationCommand() {
    role<AdminUser> {
        state<AdminMenu> {
            onText(Strings.AdminPanel.Menu.StepDuration) {
                sendTextMessage(it.chat, StepDuration.Header, replyMarkup = stepDurationKeyboard())
            }
            onDataCallbackQuery(Regex("change_step_duration \\d"), handler = {
                val step = Step(it.data.split(' ')[1].toInt())
                setState(WaitingForStepDuration(step, it.messageCallbackQueryOrThrow().message.messageId))
            })
        }
        state<WaitingForStepDuration> {
            onTransition {
                sendTextMessage(it, StepDuration.Change(state.step))
            }
            onText { message ->
                val duration = runCatching {
                    val days = message.content.text.toLong()
                    require(days > 0)
                    Duration.ofDays(days)
                }.getOrElse {
                    sendTextMessage(message.chat, Strings.AdminPanel.InvalidDurationDays)
                    return@onText
                }
                stepDurationRepository.changeDuration(state.step, duration)
                nextStepNotifier.rescheduleAll()

                editMessageReplyMarkup(
                    chat = message.chat,
                    messageId = state.messageId,
                    replyMarkup = stepDurationKeyboard()
                )

                setState(AdminMenu)
            }
        }
    }
}

private fun stepDurationKeyboard(): InlineKeyboardMarkup {
    val durations = (1..Step.LastValue).map { getStepDuration(Step(it)) }
    return inlineKeyboard {
        durations.forEach {
            row {
                dataButton(StepDuration.Button(it), "change_step_duration ${it.step.value}")
            }
        }
    }
}
