package ru.spbstu.application.admin.telegram

import com.ithersta.tgbotapi.fsm.entities.triggers.onDocument
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import org.koin.core.context.GlobalContext
import ru.spbstu.application.admin.TrendsZip
import ru.spbstu.application.auth.entities.users.AdminUser
import ru.spbstu.application.telegram.StateMachineBuilder
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.Strings.AdminPanel.UploadTrends
import ru.spbstu.application.telegram.entities.state.AdminMenu
import ru.spbstu.application.telegram.entities.state.WaitingForTrendsDocument

private val trendsZip: TrendsZip by GlobalContext.get().inject()

fun StateMachineBuilder.uploadTrendsCommand() {
    role<AdminUser> {
        state<AdminMenu> {
            onText(Strings.AdminPanel.Menu.UploadTrends) {
                setState(WaitingForTrendsDocument)
            }
        }
        state<WaitingForTrendsDocument> {
            onTransition {
                sendTextMessage(it, UploadTrends.RequireDocumentPair)
            }
            onDocument { message ->
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
                setState(AdminMenu)
            }
        }
    }
}
