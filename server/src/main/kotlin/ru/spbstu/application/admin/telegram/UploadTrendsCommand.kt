package ru.spbstu.application.admin.telegram

import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.DocumentContent
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.TrendsZip
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.AdminPanel.UploadTrends

private val trendsZip: TrendsZip by GlobalContext.get().inject()

suspend fun BehaviourContext.uploadTrendsCommand() {
    onAdminText(Strings.AdminPanel.Menu.UploadTrends) { sendHelpMessage(it.chat) }
    onAdminDocument(initialFilter = { it.content.media.fileName?.endsWith(".zip", ignoreCase = true) == true }) {
        onTrendsUploaded(it)
    }
}

private suspend fun BehaviourContext.sendHelpMessage(chat: Chat) {
    sendTextMessage(chat, UploadTrends.RequireDocumentPair)
}

private suspend fun BehaviourContext.onTrendsUploaded(message: CommonMessage<DocumentContent>) {
    val file = downloadFile(message.content.media)
    val text = when (val result = trendsZip.apply(file)) {
        TrendsZip.Result.InvalidZip -> Strings.AdminPanel.InvalidZip
        TrendsZip.Result.NoXlsx -> UploadTrends.NoXlsxInArchive
        TrendsZip.Result.InvalidXlsx -> Strings.AdminPanel.InvalidXlsx
        is TrendsZip.Result.WriteError -> UploadTrends.WriteError(result.e.message.toString())
        is TrendsZip.Result.BadFormat -> Strings.AdminPanel.InvalidSpreadsheet(result.errorRows)
        is TrendsZip.Result.MissingPictures -> UploadTrends.MissingPictures(result.filenames)
        is TrendsZip.Result.TooFewTrends -> UploadTrends.TooFewTrends(result.minimum)
        TrendsZip.Result.OK -> UploadTrends.Success
    }
    sendTextMessage(message.chat, text)
}
