package ru.spbstu.application.scamper

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.types.buttons.*
import dev.inmo.tgbotapi.extensions.utils.withContent
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.buttons.ReplyForce
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.telegram.giveBonusWithMessage
import ru.spbstu.application.telegram.IdeaGenerationStrings
import kotlin.time.Duration.Companion.seconds

suspend fun BehaviourContext.handleScamper(chat: Chat) {
    val rootMessage = sendTextMessage(chat, IdeaGenerationStrings.ScamperUI.Initializing)
    val model = ScamperModel(StandardQuestionnaire)
    coroutineScope {
        model.actions.onEach { act(it, rootMessage, this) }.launchIn(this)
        model.state.onEach { renderState(it, rootMessage) }.launchIn(this)
        waitAnswer(rootMessage, model::onQuestionAnswered).launchIn(this)
        waitCallbacks(rootMessage, model).launchIn(this)
    }
}

private suspend fun BehaviourContext.act(
    it: ScamperModel.Action,
    message: ContentMessage<TextContent>,
    coroutineScope: CoroutineScope
) = when (it) {
    is ScamperModel.Action.Ended -> {
        edit(message, text = IdeaGenerationStrings.ScamperUI.Ended, replyMarkup = null)
        val spreadsheet = Xlsx.createScamperSpreadsheet(it.answers, it.questionnaire)
        val document = spreadsheet.asMultipartFile(IdeaGenerationStrings.ScamperUI.Filename + ".xlsx")
        sendDocument(
            message.chat, document,
            replyMarkup = flatReplyKeyboard(
                resizeKeyboard = true,
                oneTimeKeyboard = true
            ) { simpleButton(IdeaGenerationStrings.BackToIdeaGeneration) }
        )
        edit(message, text = IdeaGenerationStrings.ScamperEnding, parseMode = MarkdownParseMode)
        coroutineScope.cancel()
    }
}

private fun BehaviourContext.waitCallbacks(
    rootMessage: ContentMessage<TextContent>,
    model: ScamperModel
) = callbackQueriesFlow
    .mapNotNull { it.data as? MessageDataCallbackQuery }
    .filter { it.from.id == rootMessage.chat.id }
    .filter { it.message.messageId == rootMessage.messageId }
    .onEach {
        val tokens = it.data.split(' ')
        when {
            it.data.matches(Regex("scamper_question \\d+ \\d+")) -> {
                model.onQuestionChosen(tokens[1].toInt(), tokens[2].toInt())
            }
            it.data.matches(Regex("scamper_letter \\d+")) -> {
                model.onLetterChosen(tokens[1].toInt())
            }
            it.data == "scamper_end" -> model.onEnding()
            it.data == "scamper_dismiss" -> model.onQuestionDismissed()
            it.data == "scamper_next_question" -> model.onNextQuestion()
            it.data == "scamper_next_letter" -> model.onNextLetter()
            it.data == "scamper_hide" -> model.onHideAllQuestions()
            it.data == "scamper_show" -> model.onShowAllQuestions()
            it.data == "scamper_to_letters" -> model.onToLetters()
        }
        answer(it)
        delay(0.2.seconds)
    }

private fun BehaviourContext.waitAnswer(
    rootMessage: ContentMessage<TextContent>,
    onQuestionAnswered: (String) -> BonusType?
) = messagesFlow
    .mapNotNull { (it.data as? CommonMessage<*>)?.withContent<TextContent>() }
    .filter { it.chat.id == rootMessage.chat.id }
    .onEach {
        it.delete(bot)
        val bonusType = onQuestionAnswered(it.content.text)
        if (bonusType != null) {
            val messages = giveBonusWithMessage(it.chat.id, bonusType, Step(1))
            launch {
                delay(6.seconds)
                messages.forEach { it.delete(bot) }
            }
        }
        delay(1.seconds)
    }

private suspend fun TelegramBot.renderState(
    state: ScamperModel.State,
    message: ContentMessage<TextContent>
) = when (state) {
    is ScamperModel.State.AllLetters -> renderAllLetters(message, state)
    is ScamperModel.State.LetterDetails -> renderLetterDetails(message, state)
    is ScamperModel.State.Question -> renderQuestion(message, state)
}

private suspend fun TelegramBot.renderQuestion(
    message: ContentMessage<TextContent>,
    state: ScamperModel.State.Question
) = edit(
    message = message,
    entities = IdeaGenerationStrings.ScamperUI.QuestionAsked(state.question, state.previousAnswers),
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

private suspend fun TelegramBot.renderLetterDetails(
    message: ContentMessage<TextContent>,
    state: ScamperModel.State.LetterDetails
) = edit(
    message = message,
    text = state.letter.description,
    replyMarkup = inlineKeyboard {
        row {
            dataButton(IdeaGenerationStrings.ScamperUI.BackToLetters, "scamper_to_letters")
            dataButton(IdeaGenerationStrings.ScamperUI.NextLetter, "scamper_next_letter")
        }
        if (state.showAllQuestions) {
            state.letter.questions.forEachIndexed { index, s ->
                row {
                    dataButton(s, "scamper_question ${state.letterIndex} $index")
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
                    "scamper_question ${state.letterIndex} ${0}"
                )
            }
        }
        row {
            dataButton(IdeaGenerationStrings.ScamperUI.End, "scamper_end")
        }
    },
    parseMode = MarkdownV2ParseMode
)

private suspend fun TelegramBot.renderAllLetters(
    message: ContentMessage<TextContent>,
    state: ScamperModel.State.AllLetters
) = edit(
    message = message,
    text = IdeaGenerationStrings.ScamperUI.ChooseLetter,
    replyMarkup = inlineKeyboard {
        row {
            state.letters.forEachIndexed { index, letter ->
                dataButton(letter.character.toString(), "scamper_letter $index")
            }
        }
    },
    parseMode = MarkdownParseMode
)
