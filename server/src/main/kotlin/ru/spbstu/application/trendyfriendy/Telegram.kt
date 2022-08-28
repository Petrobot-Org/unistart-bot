package ru.spbstu.application.trendyfriendy

import com.ithersta.tgbotapi.fsm.entities.StateMachine
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.*
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.core.context.GlobalContext
import ru.spbstu.application.AppConfig
import ru.spbstu.application.auth.entities.users.BaseUser
import ru.spbstu.application.auth.entities.users.SubscribedUser
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.telegram.giveBonusWithMessage
import ru.spbstu.application.telegram.IdeaGenerationStrings
import ru.spbstu.application.telegram.RoleFilterBuilder
import ru.spbstu.application.telegram.entities.state.DialogState
import ru.spbstu.application.telegram.entities.state.IdeaGenerationMenu
import ru.spbstu.application.telegram.entities.state.TrendyFriendyEndedState
import ru.spbstu.application.telegram.entities.state.TrendyFriendyState
import trendyfriendy.Idea
import java.io.ByteArrayOutputStream

private val appConfig: AppConfig by GlobalContext.get().inject()
private val stateMachine: StateMachine<DialogState, BaseUser, UserId> by GlobalContext.get().inject()

fun RoleFilterBuilder<SubscribedUser>.trendyFriendy() {
    state<TrendyFriendyState> {
        onTransition {
            sendTextMessage(
                it,
                IdeaGenerationStrings.TrendyFriendyStart,
                replyMarkup = inlineKeyboard {
                    row {
                        webAppButton(
                            IdeaGenerationStrings.TrendyFriendyOpen,
                            WebAppInfo("${appConfig.publicHostname}/trendy-friendy")
                        )
                    }
                }
            )
        }
    }
    state<TrendyFriendyEndedState> {
        onTransition {
            val document = createIdeasXlsx(state.ideas)
                .asMultipartFile(IdeaGenerationStrings.IdeasSpreadsheetName + ".xlsx")
            sendDocument(
                it,
                document,
                replyMarkup = flatReplyKeyboard(
                    resizeKeyboard = true,
                    oneTimeKeyboard = true
                ) { simpleButton(IdeaGenerationStrings.BackToIdeaGeneration) }
            )
        }
        onText(IdeaGenerationStrings.BackToIdeaGeneration) {
            setState(IdeaGenerationMenu)
        }
    }
}

suspend fun RequestsExecutor.sendTrendyFriendyIdeas(userId: Long, ideas: List<Idea>) {
    giveBonusWithMessage(userId.toChatId(), BonusType.TrendyFriendy, Step(1))
    with(stateMachine) { setState(userId.toChatId(), TrendyFriendyEndedState(ideas)) }
}

private fun createIdeasXlsx(ideas: List<Idea>): ByteArray {
    val workbook = XSSFWorkbook()
    workbook.createSheet().apply {
        createRow(0).apply {
            val style = workbook.createCellStyle().apply {
                setFont(
                    workbook.createFont().apply {
                        bold = true
                    }
                )
                wrapText = true
                heightInPoints = 3 * defaultRowHeightInPoints
            }
            listOf(
                IdeaGenerationStrings.IdeasSpreadsheetNumber,
                IdeaGenerationStrings.IdeasSpreadsheetDescription,
                IdeaGenerationStrings.IdeasSpreadsheetTechnical,
                IdeaGenerationStrings.IdeasSpreadsheetEconomical
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
