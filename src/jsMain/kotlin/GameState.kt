import trends.TrendCard

sealed interface GameState {
    object Loading : GameState
    data class Playing(
        val cards: List<TrendCard>,
        val ideasCount: Int
    ) : GameState
    data class Details(
        val card: TrendCard
    )
}
