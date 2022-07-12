package trendyfriendy

import kotlinx.serialization.Serializable

@Serializable
data class IdeaResponse(
    val ideasCount: Int
)
