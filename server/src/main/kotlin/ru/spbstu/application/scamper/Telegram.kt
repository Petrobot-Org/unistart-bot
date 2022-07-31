package ru.spbstu.application.scamper

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitDataCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.row
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.spbstu.application.telegram.IdeaGenerationStrings

suspend fun BehaviourContext.handleScamper(chat: Chat) {
    val message = sendTextMessage(chat, IdeaGenerationStrings.ScamperUI.Initializing)
    val model = ScamperModel(StandardQuestionnaire)
    coroutineScope {
        launch {
            model.actions.collect {
                when (it) {
                    is ScamperModel.Action.Ended -> {
                        edit(message, text = it.toString(), replyMarkup = null)
                        cancel()
                    }
                }
            }
        }
        launch {
            model.state.collect { renderState(it, message) }
        }
        launch {
            waitAnswer(chat, model::onQuestionAnswered)
        }
        launch {
            waitCallbacks(
                chat = chat,
                onQuestionChosen = model::onQuestionChosen,
                onLetterChosen = model::onLetterChosen,
                onEnding = model::onEnding,
                onQuestionDismissed = model::onQuestionDismissed,
                onNextQuestion = model::onNextQuestion,
                onNextLetter = model::onNextLetter
            )
        }
    }
}

private suspend fun BehaviourContext.waitCallbacks(
    chat: Chat,
    onQuestionChosen: (Int, Int) -> Unit,
    onLetterChosen: (Int) -> Unit,
    onEnding: suspend () -> Unit,
    onQuestionDismissed: () -> Unit,
    onNextQuestion: () -> Unit,
    onNextLetter: () -> Unit
) {
    waitDataCallbackQuery()
        .filter { it.from.id == chat.id }
        .collect {
            val tokens = it.data.split(' ')
            when {
                it.data.matches(Regex("scamper_question \\d+ \\d+")) -> {
                    onQuestionChosen(tokens[1].toInt(), tokens[2].toInt())
                }
                it.data.matches(Regex("scamper_letter \\d+")) -> {
                    onLetterChosen(tokens[1].toInt())
                }
                it.data == "scamper_end" -> onEnding()
                it.data == "scamper_dismiss" -> onQuestionDismissed()
                it.data == "scamper_next_question" -> onNextQuestion()
                it.data == "scamper_next_letter" -> onNextLetter()
            }
        }
}

private suspend fun BehaviourContext.waitAnswer(
    chat: Chat,
    onQuestionAnswered: (String) -> Unit
) {
    waitContentMessage()
        .filter { it.chat.id == chat.id }
        .filter { it.content is TextContent }
        .onEach { delete(chat, it.messageId) }
        .collect { onQuestionAnswered((it.content as TextContent).text) }
}

private suspend fun TelegramBot.renderState(
    it: ScamperModel.State,
    message: ContentMessage<TextContent>
) {
    when (it) {
        is ScamperModel.State.ChooseQuestion -> edit(
            message = message,
            text = it.letter.description,
            replyMarkup = inlineKeyboard {
                row {
                    it.otherLetters.forEachIndexed { index, letter ->
                        dataButton(letter.character.toString(), "scamper_letter $index")
                    }
                }
                it.letter.questions.forEachIndexed { index, s ->
                    row {
                        dataButton(s, "scamper_question ${it.letterIndex} $index")
                    }
                }
                row {
                    dataButton(IdeaGenerationStrings.ScamperUI.End, "scamper_end")
                }
            },
            parseMode = MarkdownParseMode
        )
        is ScamperModel.State.AnswerQuestion -> edit(
            message = message,
            entities = IdeaGenerationStrings.ScamperUI.QuestionAsked(it.question, it.previousAnswers),
            replyMarkup = inlineKeyboard {
                row {

                    dataButton(IdeaGenerationStrings.ScamperUI.BackToQuestions, "scamper_dismiss")
                }
                row {
                    dataButton(IdeaGenerationStrings.ScamperUI.NextQuestion, "scamper_next_question")
                    dataButton(IdeaGenerationStrings.ScamperUI.NextLetter, "scamper_next_letter")
                }
            }
        )
    }
}
