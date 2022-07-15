package ru.spbstu.application.trendyfriendy

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.*
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.core.context.GlobalContext
import ru.spbstu.application.AppConfig
import ru.spbstu.application.telegram.Strings
import trendyfriendy.Idea
import java.io.ByteArrayOutputStream

private val appConfig: AppConfig by GlobalContext.get().inject()

suspend fun BehaviourContext.sendTrendyFriendyApp(chat: Chat) {
    sendTextMessage(
        chat,
        Strings.TrendyFriendyStart,
        replyMarkup = inlineKeyboard {
            row {
                webAppButton(Strings.TrendyFriendyOpen, WebAppInfo("${appConfig.publicHostname}/trendy-friendy"))
            }
        }
    )
}

suspend fun sendTrendyFriendyIdeas(bot: TelegramBot, userId: Long, ideas: List<Idea>) {
    val document = createIdeasXlsx(ideas).asMultipartFile(Strings.IdeasSpreadsheetName + ".xlsx")
    bot.sendDocument(userId.toChatId(), document, replyMarkup = flatReplyKeyboard { simpleButton(Strings.BackToIdeaGeneration) })
}

private fun createIdeasXlsx(ideas: List<Idea>): ByteArray {
    val workbook = XSSFWorkbook()
    workbook.createSheet().apply {
        createRow(0).apply {
            val style = workbook.createCellStyle().apply {
                setFont(workbook.createFont().apply {
                    bold = true
                })
                wrapText = true
                heightInPoints = 3 * defaultRowHeightInPoints
            }
            listOf(
                Strings.IdeasSpreadsheetNumber,
                Strings.IdeasSpreadsheetDescription,
                Strings.IdeasSpreadsheetTechnical,
                Strings.IdeasSpreadsheetEconomical
            ).forEachIndexed { index, s ->
                createCell(index).apply {
                    setCellValue(s)
                    cellStyle = style
                }
            }
        }
        ideas.forEachIndexed { index, idea ->
            createRow(index + 1).apply {
                createCell(0).setCellValue((index + 1).toString())
                createCell(1).setCellValue(idea.text)
            }
        }
        autoSizeColumn(0)
        setColumnWidth(1, 60 * 256)
        setColumnWidth(2, 35 * 256)
        setColumnWidth(3, 35 * 256)
    }
    return ByteArrayOutputStream().apply {
        workbook.write(this)
    }.toByteArray()
}
