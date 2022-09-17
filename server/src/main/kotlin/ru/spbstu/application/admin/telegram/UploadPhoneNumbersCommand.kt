package ru.spbstu.application.admin.telegram

import com.ithersta.tgbotapi.fsm.entities.triggers.onDocument
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.DocumentContent
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.Xlsx
import ru.spbstu.application.admin.usecases.AddPhoneNumbersUseCase
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.users.AdminUser
import ru.spbstu.application.telegram.StateMachineBuilder
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.AdminPanel.UploadPhoneNumbers
import ru.spbstu.application.telegram.entities.state.AdminMenu
import ru.spbstu.application.telegram.entities.state.UploadPhoneNumbersState
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val zoneId: ZoneId by GlobalContext.get().inject()
private val addPhoneNumbers: AddPhoneNumbersUseCase by GlobalContext.get().inject()

fun StateMachineBuilder.uploadPhoneNumbersCommand() {
    role<AdminUser> {
        state<AdminMenu> {
            onText(Strings.AdminPanel.Menu.UploadPhoneNumbers) {
                setState(UploadPhoneNumbersState.WaitingForDocument)
            }
        }
        state<UploadPhoneNumbersState.WaitingForDocument> {
            onTransition {
                sendTextMessage(it, UploadPhoneNumbers.RequireDocument)
            }
            onDocument { message ->
                val phoneNumbers = getPhoneNumbersFromXlsx(message)
                if (phoneNumbers.isEmpty()) {
                    return@onDocument
                }
                val nonRussianPhoneNumbers = phoneNumbers.filterNot { it.isRussian() }
                if (nonRussianPhoneNumbers.isNotEmpty()) {
                    sendTextMessage(message.chat, UploadPhoneNumbers.NonRussianPhoneNumbers(nonRussianPhoneNumbers))
                }
                setState(UploadPhoneNumbersState.WaitingForStart(phoneNumbers))
            }
        }
        state<UploadPhoneNumbersState.WaitingForStart> {
            onTransition {
                sendTextMessage(it, UploadPhoneNumbers.RequireStartDate)
            }
            onText { message ->
                val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
                val start = runCatching {
                    val date = dateTimeFormatter.parse(message.content.text, LocalDate::from)
                    date.atStartOfDay(zoneId).toInstant()
                }.getOrElse {
                    sendTextMessage(message.chat, UploadPhoneNumbers.InvalidDate)
                    return@onText
                }
                setState(UploadPhoneNumbersState.WaitingForDuration(state.phoneNumbers, start))
            }
        }
        state<UploadPhoneNumbersState.WaitingForDuration> {
            onTransition {
                sendTextMessage(it, UploadPhoneNumbers.RequireDurationDays)
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
                runCatching {
                    val changes = addPhoneNumbers(state.phoneNumbers.toSet(), state.start, duration)
                    sendTextMessage(message.chat, UploadPhoneNumbers.Added(changes))
                }.onFailure {
                    sendTextMessage(message.chat, Strings.DatabaseError)
                }
            }
        }
    }
}

suspend fun RequestsExecutor.getPhoneNumbersFromXlsx(message: CommonMessage<DocumentContent>): List<PhoneNumber> {
    return when (val result = Xlsx.parsePhoneNumbers(downloadFile(message.content.media).inputStream())) {
        is Xlsx.Result.InvalidFile -> {
            sendTextMessage(message.chat, Strings.AdminPanel.InvalidXlsx)
            return emptyList()
        }

        is Xlsx.Result.BadFormat -> {
            sendTextMessage(message.chat, Strings.AdminPanel.InvalidSpreadsheet(result.errorRows))
            return emptyList()
        }

        is Xlsx.Result.OK -> result.value
    }
}
