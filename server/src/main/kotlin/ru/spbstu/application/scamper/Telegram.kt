package ru.spbstu.application.scamper

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitDataCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.types.buttons.*
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.telegram.giveBonusWithMessage
import ru.spbstu.application.telegram.IdeaGenerationStrings

suspend fun BehaviourContext.handleScamper(chat: Chat) {
    val message = sendTextMessage(chat, IdeaGenerationStrings.ScamperUI.Initializing)
    val questionnaire = StandardQuestionnaire
    val model = ScamperModel(questionnaire)
    coroutineScope {
        val coroutineScope = this
        launch {
            model.actions.collect {
                when (it) {
                    is ScamperModel.Action.Ended -> {
                        edit(message, text = IdeaGenerationStrings.ScamperUI.Ended, replyMarkup = null)
                        val spreadsheet = Xlsx.createScamperSpreadsheet(it.answers, questionnaire)
                        val document = spreadsheet.asMultipartFile(IdeaGenerationStrings.ScamperUI.Filename + ".xlsx")
                        sendDocument(chat, document, replyMarkup = flatReplyKeyboard(
                            resizeKeyboard = true,
                            oneTimeKeyboard = true
                        ) { simpleButton(IdeaGenerationStrings.BackToIdeaGeneration) })
                        edit(message, text = IdeaGenerationStrings.ScamperEnding)
                        coroutineScope.cancel()
                    }
                }
            }
        }
        launch { model.state.collect { renderState(it, message) } }
        launch { waitAnswer(chat, model::onQuestionAnswered) }
        launch { waitCallbacks(chat, model) }
    }
}

private suspend fun BehaviourContext.waitCallbacks(
    chat: Chat,
    model: ScamperModel
) {
    waitDataCallbackQuery()
        .filter { it.from.id == chat.id }
        .collect {
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
            }
        }
}

private suspend fun BehaviourContext.waitAnswer(
    chat: Chat,
    onQuestionAnswered: (String) -> BonusType?
) {
    waitContentMessage()
        .filter { it.chat.id == chat.id }
        .filter { it.content is TextContent }
        .onEach { delete(chat, it.messageId) }
        .collect {
            val bonusType = onQuestionAnswered((it.content as TextContent).text)
            if (bonusType != null) {
                giveBonusWithMessage(chat.id, bonusType, Step(1), true)
            }
        }
}

private suspend fun TelegramBot.renderState(
    it: ScamperModel.State,
    message: ContentMessage<TextContent>
) {
    when (it) {
        is ScamperModel.State.LetterDetails -> edit(
            message = message,
            text = it.letter.description,
            replyMarkup = inlineKeyboard {
                row {
                    it.otherLetters.forEachIndexed { index, letter ->
                        dataButton(letter.character.toString(), "scamper_letter $index")
                    }
                }
                if (it.showAllQuestions) {
                    it.letter.questions.forEachIndexed { index, s ->
                        row {
                            dataButton(s, "scamper_question ${it.letterIndex} $index")
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
                            "scamper_question ${it.letterIndex} ${0}"
                        )
                    }
                }
                row {
                    dataButton(IdeaGenerationStrings.ScamperUI.End, "scamper_end")
                }
            },
            parseMode = MarkdownParseMode
        )
        is ScamperModel.State.Question -> edit(
            message = message,
            entities = IdeaGenerationStrings.ScamperUI.QuestionAsked(it.question, it.previousAnswers),
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
}
