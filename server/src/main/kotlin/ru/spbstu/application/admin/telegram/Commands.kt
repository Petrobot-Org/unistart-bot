package ru.spbstu.application.admin.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.messageCallbackQueryOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.Xlsx
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.repository.StepDurationRepository
import ru.spbstu.application.steps.usecases.GetStepDurationUseCase
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.waitDocumentFrom
import ru.spbstu.application.telegram.waitTextFrom
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val getStepDuration: GetStepDurationUseCase by GlobalContext.get().inject()
private val stepDurationRepository: StepDurationRepository by GlobalContext.get().inject()

suspend fun BehaviourContext.adminCommands() {
    onAdminCommand("uploadnumbers") { uploadPhoneNumbers(it) }
    onAdminCommand("stepduration") { showStepDurations(it) }
    onAdminDataCallbackQuery(Regex("change_step_duration \\d")) { changeStepDuration(it) }
}

suspend fun BehaviourContext.uploadPhoneNumbers(message: CommonMessage<TextContent>) {
    val phoneNumbers = waitPhoneNumbers(message.chat)
    val startInstant = waitStartInstant(message.chat)
    val duration = waitDuration(message.chat)

    TODO()
}

private suspend fun BehaviourContext.waitDuration(chat: Chat): Duration {
    return waitTextFrom(chat, SendTextMessage(chat.id, Strings.RequireDurationDays))
        .map {
            try {
                val days = it.text.toLong()
                require(days > 0)
                Duration.ofDays(days)
            } catch (e: Exception) {
                null
            }
        }
        .onEach { if (it == null) sendTextMessage(chat, Strings.InvalidDurationDays) }
        .firstNotNull()
}

private suspend fun BehaviourContext.waitStartInstant(chat: Chat): Instant {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
    return waitTextFrom(chat, SendTextMessage(chat.id, Strings.RequireStartDate))
        .map {
            try {
                val date = dateTimeFormatter.parse(it.text, LocalDate::from)
                date.atTime(0, 0).toInstant(ZoneOffset.UTC)
            } catch (e: DateTimeParseException) {
                null
            }
        }
        .onEach { if (it == null) sendTextMessage(chat, Strings.InvalidDate) }
        .firstNotNull()
}

private suspend fun BehaviourContext.waitPhoneNumbers(chat: Chat): List<PhoneNumber> {
    return waitDocumentFrom(chat, SendTextMessage(chat.id, Strings.RequirePhoneNumbersDocument))
        .map {
            val result = downloadFile(it.media).inputStream().use { inputStream ->
                Xlsx.parsePhoneNumbers(inputStream)
            }
            when (result) {
                is Xlsx.Result.BadFormat -> {
                    sendTextMessage(chat, Strings.InvalidSpreadsheet(result.errorRows))
                    null
                }
                is Xlsx.Result.OK -> result.value
            }
        }
        .firstNotNull()
}

suspend fun BehaviourContext.showStepDurations(message: CommonMessage<TextContent>) {
    sendTextMessage(message.chat, Strings.StepDurationsHeader, replyMarkup = stepDurationKeyboard())
}

@OptIn(PreviewFeature::class)
suspend fun BehaviourContext.changeStepDuration(dataCallbackQuery: DataCallbackQuery) {
    val step = Step(dataCallbackQuery.data.split(' ')[1].toLong())
    val duration = waitTextFrom(
        dataCallbackQuery.from,
        SendTextMessage(dataCallbackQuery.from.id, Strings.ChangeStepDuration(step))
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
        .onEach { if (it == null) sendTextMessage(dataCallbackQuery.from, Strings.InvalidDurationDays) }
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
                dataButton(Strings.StepDurationButton(it), "change_step_duration ${it.step.value}")
            }
        }
    }
}
