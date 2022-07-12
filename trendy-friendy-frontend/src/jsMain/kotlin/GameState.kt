import trendyfriendy.TrendCard

sealed interface GameState {
    val loadingState: LoadingState

    object Loading : GameState {
        override val loadingState = LoadingState(addingIdea = true, gettingCards = true, finishing = true)
    }

    data class Playing(
        val cards: List<TrendCard>,
        val ideasCount: Int,
        val ideaInput: String,
        override val loadingState: LoadingState
    ) : GameState
    data class Details(
        val card: TrendCard,
        override val loadingState: LoadingState
    ) : GameState
    data class ChooseSet(
        val sets: Set<String>,
        val selectedSets: Set<String>,
        override val loadingState: LoadingState
    ) : GameState
}

data class LoadingState(
    val addingIdea: Boolean,
    val gettingCards: Boolean,
    val finishing: Boolean
)
