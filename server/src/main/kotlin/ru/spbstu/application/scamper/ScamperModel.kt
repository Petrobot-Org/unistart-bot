package ru.spbstu.application.scamper

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.spbstu.application.steps.entities.BonusType

class ScamperModel(private val questionnaire: Questionnaire) {
    private val position = MutableStateFlow(Position())
    private val answers = MutableStateFlow(emptyList<ScamperAnswer>())
    private val _actions = Channel<Action>(Channel.BUFFERED)

    val actions = _actions.consumeAsFlow()

    val state = combine(position, answers) { position, answers ->
        val letter = questionnaire.letters.getOrElse(position.letterIndex) { questionnaire.letters.first() }
        val question = position.questionIndex?.let { letter.questions.getOrNull(it) }
        if (question == null) {
            State.ChooseQuestion(letter, questionnaire.letters, position.letterIndex)
        } else {
            State.AnswerQuestion(
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
        val answer = ScamperAnswer(position.value.letterIndex, position.value.questionIndex ?: return null, text)
        answers.value += answer
        return questionnaire.letters[position.value.letterIndex].bonusType
    }

    fun onNextQuestion() {
        val letter = questionnaire.letters[position.value.letterIndex]
        val nextIndex = position.value.questionIndex?.plus(1) ?: 0
        if (nextIndex in letter.questions.indices) {
            position.value = position.value.copy(questionIndex = nextIndex)
        } else {
            onNextLetter()
        }
    }

    fun onNextLetter() {
        position.value = Position((position.value.letterIndex + 1) % questionnaire.letters.size)
    }

    suspend fun onEnding() {
        _actions.send(Action.Ended(answers.value))
    }

    sealed interface State {
        data class ChooseQuestion(
            val letter: ScamperLetter,
            val otherLetters: List<ScamperLetter>,
            val letterIndex: Int
        ) : State

        data class AnswerQuestion(
            val question: String,
            val previousAnswers: List<String>
        ) : State
    }

    sealed interface Action {
        data class Ended(val answers: List<ScamperAnswer>) : Action
    }

    data class Position(
        val letterIndex: Int = 0,
        val questionIndex: Int? = null
    )
}
