package ru.spbstu.application.scamper

import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.telegram.entities.state.Position
import ru.spbstu.application.telegram.entities.state.ScamperAnswer
import ru.spbstu.application.telegram.entities.state.ScamperState

val questionnaire = StandardQuestionnaire

fun ScamperState.onQuestionDismissed() = copy(
    position = position.copy(questionIndex = null)
)

fun ScamperState.onQuestionChosen(letterIndex: Int, questionIndex: Int) = copy(
    position = Position(letterIndex, questionIndex)
)

fun ScamperState.onLetterChosen(letterIndex: Int) = copy(
    position = Position(letterIndex)
)

fun ScamperState.onQuestionAnswered(text: String): Pair<ScamperState, BonusType>? {
    val letterIndex = position.letterIndex ?: return null
    val questionIndex = position.questionIndex ?: return null
    val answer = ScamperAnswer(letterIndex, questionIndex, text)
    return copy(
        answers = answers + answer
    ) to questionnaire.letters[letterIndex].bonusType
}

fun ScamperState.onNextQuestion(): ScamperState {
    val letter = questionnaire.letters[position.letterIndex ?: return this]
    val nextIndex = position.questionIndex?.plus(1) ?: 0
    return if (nextIndex in letter.questions.indices) {
        copy(
            position = position.copy(questionIndex = nextIndex)
        )
    } else {
        onNextLetter()
    }
}

fun ScamperState.onNextLetter() = copy(
    position = Position((position.letterIndex?.plus(1) ?: 0) % questionnaire.letters.size)
)

fun ScamperState.onHideAllQuestions() = copy(
    showAllQuestions = false
)

fun ScamperState.onShowAllQuestions() = copy(
    showAllQuestions = true
)

fun ScamperState.onToLetters() = copy(
    position = Position()
)

fun ScamperState.onEnding() = copy(
    ended = true
)
