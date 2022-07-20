package ru.spbstu.application.trendyfriendy

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import trendyfriendy.TrendCard
import java.io.FileInputStream

@Serializable
data class TrendyFriendyConfig(
    @SerialName("cards_per_game") val cardsPerGame: Int = 3,
    val sets: Map<String, List<TrendCard>>
) {
    class HotReloader(val configPath: String) {
        var config = load()

        fun reload() {
            config = load()
        }

        private fun load(): TrendyFriendyConfig? {
            return FileInputStream(configPath).use {
                try {
                    Yaml.default.decodeFromStream(it)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}
