package trendyfriendy

import kotlinx.serialization.Serializable

@Serializable
data class TrendCardSet(
    val displayName: String,
    val id: Long
)
