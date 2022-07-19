package ru.spbstu.application.admin.telegram

import dev.inmo.micro_utils.coroutines.firstNotNull
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.chat.Chat
import kotlinx.coroutines.flow.map
import ru.spbstu.application.admin.Xlsx
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.AdminPanel.UploadTrends
import ru.spbstu.application.telegram.waitDocumentFrom

suspend fun BehaviourContext.uploadTrendsCommand() {
    onAdminText(Strings.AdminPanel.Menu.UploadTrends) { uploadTrends(it.chat) }
}

private suspend fun BehaviourContext.uploadTrends(chat: Chat) {
    val sets = waitDocumentFrom(chat, SendTextMessage(chat.id, UploadTrends.RequireDocument))
        .map {
            val result = downloadFile(it.media).inputStream().use { inputStream ->
                Xlsx.parseTrends(inputStream, "/static/trends/")
            }
            when (result) {
                is Xlsx.Result.BadFormat -> {
                    sendTextMessage(chat, Strings.AdminPanel.InvalidSpreadsheet(result.errorRows))
                    null
                }
                is Xlsx.Result.OK -> result.value
            }
        }
        .firstNotNull()

    sendTextMessage(chat, sets.toString())
}
