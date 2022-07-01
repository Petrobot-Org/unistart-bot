import trendyfriendy.TrendCard
import trendyfriendy.TrendCardSet

sealed interface GameState {
    val loadingState: LoadingState

    object Loading : GameState {
        override val loadingState = LoadingState(true, true, true)
    }

    data class Playing(
        val cards: List<TrendCard>,
        val ideasCount: Int,
        override val loadingState: LoadingState
    ) : GameState
    data class Details(
        val card: TrendCard,
        override val loadingState: LoadingState
    ) : GameState
    data class ChooseSet(
        val sets: List<TrendCardSet>,
        val selectedSets: Set<TrendCardSet>,
        override val loadingState: LoadingState
    ) : GameState
}

data class LoadingState(
    val addingIdea: Boolean,
    val gettingCards: Boolean,
    val finishing: Boolean
)
