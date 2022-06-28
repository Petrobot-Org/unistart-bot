import trends.TrendCard

sealed interface GameState {
    object Loading : GameState
    data class Playing(
        val cards: List<TrendCard>,
        val ideas: List<String>
    ) : GameState
}
