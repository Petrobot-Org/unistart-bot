package ru.spbstu.application.telegram.entities.state

import kotlinx.serialization.Serializable

@Serializable
object IdeaGenerationMenu : DialogState

@Serializable
data class IdeaGenerationDescriptionState(
    val method: String,
    val index: Int = 0
) : DialogState
