package ru.spbstu.application.admin.telegram

import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.chat.Chat
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.Xlsx
import ru.spbstu.application.steps.repository.CompletedStepRepository
import ru.spbstu.application.telegram.Strings

private val completedStepRepository: CompletedStepRepository by GlobalContext.get().inject()

suspend fun BehaviourContext.statisticsSpreadsheetCommand() {
    onAdminText(Strings.AdminPanel.Menu.StatisticsSpreadsheet) { sendStatisticsSpreadsheet(it.chat) }
}

private suspend fun BehaviourContext.sendStatisticsSpreadsheet(chat: Chat) {
    val users = completedStepRepository.getUsersWithCompletedSteps()
    val spreadsheet = Xlsx.createStatisticsSpreadsheet(users)
    sendDocument(chat, spreadsheet.asMultipartFile(Strings.StatisticsSpreadsheetName + ".xlsx"))
}
