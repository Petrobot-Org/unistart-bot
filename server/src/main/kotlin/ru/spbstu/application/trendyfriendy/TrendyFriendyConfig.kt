package ru.spbstu.application.trendyfriendy

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import trendyfriendy.TrendCard

@Serializable
data class TrendyFriendyConfig(
    @SerialName("cards_per_game") val cardsPerGame: Int,
    val sets: Map<String, List<TrendCard>>
)

fun readTrendyFriendyConfig(): TrendyFriendyConfig {
    val inputStream = TrendyFriendyConfig::class.java.getResourceAsStream("/trends.yaml")!!
    return Yaml.default.decodeFromStream(inputStream)
}
