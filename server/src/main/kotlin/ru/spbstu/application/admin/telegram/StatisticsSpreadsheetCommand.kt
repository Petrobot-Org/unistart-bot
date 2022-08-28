package ru.spbstu.application.admin.telegram

import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.Xlsx
import ru.spbstu.application.auth.entities.users.AdminUser
import ru.spbstu.application.auth.usecases.IsNonRootAdminUseCase
import ru.spbstu.application.steps.repository.CompletedStepRepository
import ru.spbstu.application.telegram.StateMachineBuilder
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.entities.state.AdminMenu
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val isAdmin: IsNonRootAdminUseCase by GlobalContext.get().inject()
private val completedStepRepository: CompletedStepRepository by GlobalContext.get().inject()
private val zoneId: ZoneId by GlobalContext.get().inject()

fun StateMachineBuilder.statisticsSpreadsheetCommand() {
    role<AdminUser> {
        state<AdminMenu> {
            onText(Strings.AdminPanel.Menu.StatisticsSpreadsheet) { message ->
                val users = completedStepRepository.getUsersWithCompletedSteps()
                    .filterNot { isAdmin(it.user.id) }
                val spreadsheet = Xlsx.createStatisticsSpreadsheet(users)
                val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
                val timestamp = dateTimeFormatter.format(Instant.now().atZone(zoneId))
                sendDocument(
                    message.chat,
                    spreadsheet.asMultipartFile("${Strings.StatisticsSpreadsheetName} $timestamp.xlsx")
                )
                setState(AdminMenu)
            }
        }
    }
}
