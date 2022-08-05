package ru.spbstu.application.trendyfriendy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import trendyfriendy.TrendCard

@Serializable
class TrendyFriendyConfig private constructor(
    @SerialName("cards_per_game") val cardsPerGame: Int,
    val sets: Map<String, List<TrendCard>>
) {
    companion object {
        sealed interface Result {
            data class OK(val value: TrendyFriendyConfig) : Result
            data class TooFewTrends(val minimum: Int) : Result
        }

        fun of(sets: Map<String, List<TrendCard>>, cardsPerGame: Int = 3): Result {
            return if (isValid(sets, cardsPerGame)) {
                Result.OK(TrendyFriendyConfig(cardsPerGame, sets))
            } else {
                Result.TooFewTrends(cardsPerGame)
            }
        }

        private fun isValid(sets: Map<String, List<TrendCard>>, cardsPerGame: Int): Boolean {
            return sets.isNotEmpty() && sets.all { it.value.size >= cardsPerGame }
        }
    }
}
