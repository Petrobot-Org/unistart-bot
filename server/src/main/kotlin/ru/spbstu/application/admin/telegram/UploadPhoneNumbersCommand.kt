package ru.spbstu.application.admin.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.Xlsx
import ru.spbstu.application.admin.usecases.AddPhoneNumbersUseCase
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.AdminPanel.UploadPhoneNumbers
import ru.spbstu.application.telegram.waitDocumentFrom
import ru.spbstu.application.telegram.waitTextFrom
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val zoneId: ZoneId by GlobalContext.get().inject()
private val addPhoneNumbers: AddPhoneNumbersUseCase by GlobalContext.get().inject()

suspend fun BehaviourContext.uploadPhoneNumbersCommand() {
    onAdminText(Strings.AdminPanel.Menu.UploadPhoneNumbers) { uploadPhoneNumbers(it) }
}

private suspend fun BehaviourContext.uploadPhoneNumbers(message: CommonMessage<TextContent>) {
    val phoneNumbers = waitPhoneNumbers(message.chat)
    val startInstant = waitStartInstant(message.chat)
    val duration = waitDuration(message.chat)
    try {
        val rowsAffected = addPhoneNumbers(phoneNumbers.toSet(), startInstant, duration)
        sendTextMessage(message.chat, UploadPhoneNumbers.Added(rowsAffected.toLong()))
    } catch (e: Exception) {
        sendTextMessage(message.chat, Strings.DatabaseError)
        throw e
    }
}

private suspend fun BehaviourContext.waitDuration(chat: Chat): Duration {
    return waitTextFrom(chat, SendTextMessage(chat.id, UploadPhoneNumbers.RequireDurationDays))
        .map {
            try {
                val days = it.text.toLong()
                require(days > 0)
                Duration.ofDays(days)
            } catch (e: Exception) {
                null
            }
        }
        .onEach { if (it == null) sendTextMessage(chat, Strings.AdminPanel.InvalidDurationDays) }
        .firstNotNull()
}

private suspend fun BehaviourContext.waitStartInstant(chat: Chat): Instant {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
    return waitTextFrom(chat, SendTextMessage(chat.id, UploadPhoneNumbers.RequireStartDate))
        .map {
            try {
                val date = dateTimeFormatter.parse(it.text, LocalDate::from)
                date.atStartOfDay(zoneId).toInstant()
            } catch (e: DateTimeParseException) {
                null
            }
        }
        .onEach { if (it == null) sendTextMessage(chat, UploadPhoneNumbers.InvalidDate) }
        .firstNotNull()
}

private suspend fun BehaviourContext.waitPhoneNumbers(chat: Chat): List<PhoneNumber> {
    return waitDocumentFrom(chat, SendTextMessage(chat.id, UploadPhoneNumbers.RequireDocument))
        .map {
            val result = downloadFile(it.media).inputStream().use { inputStream ->
                Xlsx.parsePhoneNumbers(inputStream)
            }
            when (result) {
                is Xlsx.Result.InvalidFile -> {
                    sendTextMessage(chat, Strings.AdminPanel.InvalidXlsx)
                    null
                }
                is Xlsx.Result.BadFormat -> {
                    sendTextMessage(chat, Strings.AdminPanel.InvalidSpreadsheet(result.errorRows))
                    null
                }
                is Xlsx.Result.OK -> result.value
            }
        }
        .firstNotNull()
}
