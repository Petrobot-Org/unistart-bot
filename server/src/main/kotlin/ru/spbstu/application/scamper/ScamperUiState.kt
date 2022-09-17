package ru.spbstu.application.scamper

import ru.spbstu.application.telegram.entities.state.ScamperAnswer
import ru.spbstu.application.telegram.entities.state.ScamperState

sealed interface ScamperUiState {
    data class AllLetters(
        val letters: List<ScamperLetter>
    ) : ScamperUiState

    data class LetterDetails(
        val letter: ScamperLetter,
        val letterIndex: Int,
        val showAllQuestions: Boolean
    ) : ScamperUiState

    data class Question(
        val question: String,
        val previousAnswers: List<String>
    ) : ScamperUiState

    data class Ended(
        val answers: List<ScamperAnswer>
    ) : ScamperUiState
}

fun ScamperState.toUiState(): ScamperUiState {
    if (ended) {
        return ScamperUiState.Ended(answers)
    }
    val letter = position.letterIndex?.let { questionnaire.letters[it] }
    val question = position.questionIndex?.let { letter?.questions?.getOrNull(it) }
    return if (letter == null) {
        ScamperUiState.AllLetters(questionnaire.letters)
    } else if (question == null) {
        ScamperUiState.LetterDetails(letter, position.letterIndex!!, showAllQuestions)
    } else {
        ScamperUiState.Question(
            question = question,
            previousAnswers = answers
                .filter { it.letterIndex == position.letterIndex && it.questionIndex == position.questionIndex }
                .map { it.text }
        )
    }
}
