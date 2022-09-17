package ru.spbstu.application.scamper

import com.ithersta.tgbotapi.fsm.entities.triggers.OnStateChangedContext
import com.ithersta.tgbotapi.fsm.entities.triggers.onDataCallbackQuery
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import com.ithersta.tgbotapi.fsm.entities.triggers.onTransition
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.*
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.spbstu.application.auth.entities.users.BaseUser
import ru.spbstu.application.auth.entities.users.SubscribedUser
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.telegram.giveBonusWithMessage
import ru.spbstu.application.telegram.IdeaGenerationStrings
import ru.spbstu.application.telegram.RoleFilterBuilder
import ru.spbstu.application.telegram.entities.state.DialogState
import ru.spbstu.application.telegram.entities.state.IdeaGenerationMenu
import ru.spbstu.application.telegram.entities.state.ScamperState
import kotlin.time.Duration.Companion.seconds

private typealias ScamperOnStateChangedContext = OnStateChangedContext<DialogState, BaseUser, ScamperState, SubscribedUser>

suspend fun OnStateChangedContext<DialogState, *, *, *>.startScamper(chat: ChatId) {
    val rootMessage = sendTextMessage(chat, IdeaGenerationStrings.ScamperUI.Initializing)
    setState(ScamperState(rootMessage.messageId))
}

fun RoleFilterBuilder<SubscribedUser>.scamper() {
    state<ScamperState> {
        onTransition {
            render(it)
        }
        onText(IdeaGenerationStrings.BackToIdeaGeneration) {
            setState(IdeaGenerationMenu)
        }
        onText { message ->
            message.delete(this)
            val (state, bonusType) = state.onQuestionAnswered(message.content.text) ?: return@onText
            val messages = giveBonusWithMessage(message.chat.id, bonusType, Step(1))
            coroutineScope.launch {
                delay(6.seconds)
                messages.forEach { it.delete(this@onText) }
            }
            setState(state)
            delay(1.seconds)
        }
        onDataCallbackQuery(Regex(".+"), handler = {
            val tokens = it.data.split(' ')
            val newState = when {
                it.data.matches(Regex("scamper_question \\d+ \\d+")) -> {
                    state.onQuestionChosen(tokens[1].toInt(), tokens[2].toInt())
                }

                it.data.matches(Regex("scamper_letter \\d+")) -> {
                    state.onLetterChosen(tokens[1].toInt())
                }

                it.data == "scamper_end" -> state.onEnding()
                it.data == "scamper_dismiss" -> state.onQuestionDismissed()
                it.data == "scamper_next_question" -> state.onNextQuestion()
                it.data == "scamper_next_letter" -> state.onNextLetter()
                it.data == "scamper_hide" -> state.onHideAllQuestions()
                it.data == "scamper_show" -> state.onShowAllQuestions()
                it.data == "scamper_to_letters" -> state.onToLetters()
                else -> return@onDataCallbackQuery
            }
            setState(newState)
            answer(it)
            delay(0.2.seconds)
        })
    }
}

private suspend fun ScamperOnStateChangedContext.render(userId: UserId) {
    when (val uiState = state.toUiState()) {
        is ScamperUiState.AllLetters -> renderAllLetters(userId, uiState)
        is ScamperUiState.LetterDetails -> renderLetterDetails(userId, uiState)
        is ScamperUiState.Question -> renderQuestion(userId, uiState)
        is ScamperUiState.Ended -> renderEnded(userId, uiState)
    }
}

private suspend fun ScamperOnStateChangedContext.renderEnded(userId: UserId, uiState: ScamperUiState.Ended) {
    editMessageText(
        chatId = userId,
        messageId = state.messageId,
        text = IdeaGenerationStrings.ScamperUI.Ended,
        replyMarkup = null
    )
    val spreadsheet = Xlsx.createScamperSpreadsheet(uiState.answers, questionnaire)
    val document = spreadsheet.asMultipartFile(IdeaGenerationStrings.ScamperUI.Filename + ".xlsx")
    sendDocument(
        userId, document,
        replyMarkup = flatReplyKeyboard(
            resizeKeyboard = true,
            oneTimeKeyboard = true
        ) { simpleButton(IdeaGenerationStrings.BackToIdeaGeneration) }
    )
    editMessageText(
        chatId = userId,
        messageId = state.messageId,
        text = IdeaGenerationStrings.ScamperEnding,
        parseMode = MarkdownParseMode
    )
}

private suspend fun ScamperOnStateChangedContext.renderQuestion(userId: UserId, uiState: ScamperUiState.Question) {
    editMessageText(
        chatId = userId,
        messageId = state.messageId,
        entities = IdeaGenerationStrings.ScamperUI.QuestionAsked(uiState.question, uiState.previousAnswers),
        replyMarkup = inlineKeyboard {
            row {
                dataButton(IdeaGenerationStrings.ScamperUI.BackToQuestions, "scamper_dismiss")
            }
            row {
                dataButton(IdeaGenerationStrings.ScamperUI.NextLetter, "scamper_next_letter")
                dataButton(IdeaGenerationStrings.ScamperUI.NextQuestion, "scamper_next_question")
            }
        }
    )
}

private suspend fun ScamperOnStateChangedContext.renderLetterDetails(
    userId: UserId,
    uiState: ScamperUiState.LetterDetails
) {
    editMessageText(
        chatId = userId,
        messageId = state.messageId,
        text = uiState.letter.description,
        replyMarkup = inlineKeyboard {
            row {
                dataButton(IdeaGenerationStrings.ScamperUI.BackToLetters, "scamper_to_letters")
                dataButton(IdeaGenerationStrings.ScamperUI.NextLetter, "scamper_next_letter")
            }
            if (uiState.showAllQuestions) {
                uiState.letter.questions.forEachIndexed { index, s ->
                    row {
                        dataButton(s, "scamper_question ${uiState.letterIndex} $index")
                    }
                }
                row {
                    dataButton(IdeaGenerationStrings.ScamperUI.HideAllQuestions, "scamper_hide")
                }
            } else {
                row {
                    dataButton(IdeaGenerationStrings.ScamperUI.ShowAllQuestions, "scamper_show")
                    dataButton(
                        IdeaGenerationStrings.ScamperUI.ToFirstQuestion,
                        "scamper_question ${uiState.letterIndex} ${0}"
                    )
                }
            }
            row {
                dataButton(IdeaGenerationStrings.ScamperUI.End, "scamper_end")
            }
        },
        parseMode = MarkdownV2ParseMode
    )
}

private suspend fun ScamperOnStateChangedContext.renderAllLetters(userId: UserId, uiState: ScamperUiState.AllLetters) {
    editMessageText(
        chatId = userId,
        messageId = state.messageId,
        text = IdeaGenerationStrings.ScamperUI.ChooseLetter,
        replyMarkup = inlineKeyboard {
            row {
                uiState.letters.forEachIndexed { index, letter ->
                    dataButton(letter.character.toString(), "scamper_letter $index")
                }
            }
        },
        parseMode = MarkdownParseMode
    )
}
