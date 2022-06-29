import trendyfriendy.TrendCard

sealed interface GameState {
    object Loading : GameState
    data class Playing(
        val cards: List<TrendCard>,
        val ideasCount: Int,
        val loadingState: LoadingState
    ) : GameState
    data class Details(
        val card: TrendCard
    ) : GameState
}

data class LoadingState(
    val addingIdea: Boolean
)
