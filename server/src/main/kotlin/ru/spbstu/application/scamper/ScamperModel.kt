package ru.spbstu.application.scamper

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.spbstu.application.steps.entities.BonusType

class ScamperModel(private val questionnaire: Questionnaire) {
    private val position = MutableStateFlow(Position())
    private val showAllQuestions = MutableStateFlow(false)
    private val answers = MutableStateFlow(emptyList<ScamperAnswer>())
    private val _actions = Channel<Action>(Channel.BUFFERED)

    val actions = _actions.consumeAsFlow()

    val state = combine(position, answers, showAllQuestions) { position, answers, showAllQuestions ->
        val letter = position.letterIndex?.let { questionnaire.letters[it] }
        val question = position.questionIndex?.let { letter?.questions?.getOrNull(it) }
        if (letter == null) {
            State.AllLetters(questionnaire.letters)
        } else if (question == null) {
            State.LetterDetails(letter, position.letterIndex, showAllQuestions)
        } else {
            State.Question(
                question = question,
                previousAnswers = answers
                    .filter { it.letterIndex == position.letterIndex && it.questionIndex == position.questionIndex }
                    .map { it.text }
            )
        }
    }.distinctUntilChanged()

    fun onQuestionDismissed() {
        position.value = position.value.copy(questionIndex = null)
    }

    fun onQuestionChosen(letterIndex: Int, questionIndex: Int) {
        position.value = Position(letterIndex, questionIndex)
    }

    fun onLetterChosen(letterIndex: Int) {
        position.value = Position(letterIndex)
    }

    fun onQuestionAnswered(text: String): BonusType? {
        val letterIndex = position.value.letterIndex ?: return null
        val questionIndex = position.value.questionIndex ?: return null
        val answer = ScamperAnswer(letterIndex, questionIndex, text)
        answers.value += answer
        return questionnaire.letters[letterIndex].bonusType
    }

    fun onNextQuestion() {
        val letter = questionnaire.letters[position.value.letterIndex ?: return]
        val nextIndex = position.value.questionIndex?.plus(1) ?: 0
        if (nextIndex in letter.questions.indices) {
            position.value = position.value.copy(questionIndex = nextIndex)
        } else {
            onNextLetter()
        }
    }

    fun onNextLetter() {
        position.value = Position((position.value.letterIndex?.plus(1) ?: 0) % questionnaire.letters.size)
    }

    suspend fun onEnding() {
        _actions.send(Action.Ended(answers.value, questionnaire))
    }

    fun onHideAllQuestions() {
        showAllQuestions.value = false
    }

    fun onShowAllQuestions() {
        showAllQuestions.value = true
    }

    fun onToLetters() {
        position.value = Position()
    }

    sealed interface State {
        data class AllLetters(
            val letters: List<ScamperLetter>
        ) : State

        data class LetterDetails(
            val letter: ScamperLetter,
            val letterIndex: Int,
            val showAllQuestions: Boolean
        ) : State

        data class Question(
            val question: String,
            val previousAnswers: List<String>
        ) : State
    }

    sealed interface Action {
        data class Ended(val answers: List<ScamperAnswer>, val questionnaire: Questionnaire) : Action
    }

    data class Position(
        val letterIndex: Int? = null,
        val questionIndex: Int? = null
    )
}
