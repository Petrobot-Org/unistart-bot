package trendyfriendy

import kotlinx.serialization.Serializable

@Serializable
data class TrendCard(
    val name: String,
    val description: String,
    val filename: String
)

val TrendCard.url get() = "/trends/$filename"
