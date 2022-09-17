package ru.spbstu.application.telegram.entities.state

import dev.inmo.tgbotapi.types.MessageIdentifier
import kotlinx.serialization.Serializable

@Serializable
data class ScamperState(
    val messageId: MessageIdentifier,
    val position: Position = Position(),
    val showAllQuestions: Boolean = false,
    val answers: List<ScamperAnswer> = emptyList(),
    val ended: Boolean = false
) : DialogState

@Serializable
data class Position(
    val letterIndex: Int? = null,
    val questionIndex: Int? = null
)

@Serializable
data class ScamperAnswer(
    val letterIndex: Int,
    val questionIndex: Int,
    val text: String
)
