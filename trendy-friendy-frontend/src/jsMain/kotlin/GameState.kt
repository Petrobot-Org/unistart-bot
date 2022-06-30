import trendyfriendy.TrendCard
import trendyfriendy.TrendCardSet

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
    data class ChooseSet(
        val sets: List<TrendCardSet>,
        val selectedSets: Set<TrendCardSet>,
        val loadingState: LoadingState
    ) : GameState
}

data class LoadingState(
    val addingIdea: Boolean,
    val gettingCards: Boolean
)
