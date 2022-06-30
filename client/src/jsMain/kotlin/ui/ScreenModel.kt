package ui

import GameClient
import GameState
import LoadingState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.inmo.tgbotapi.webapps.webApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import trendyfriendy.Idea
import trendyfriendy.TrendCard
import trendyfriendy.TrendCardSet

class ScreenModel(
    private val client: GameClient
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var sets = emptyList<TrendCardSet>()
    private var loadedInitialState by mutableStateOf(false)
    private var cards by mutableStateOf<List<TrendCard>>(emptyList())
    private var ideasCount by mutableStateOf(0)
    private var pageIndex by mutableStateOf(0)
    private var loadingAddIdea by mutableStateOf(0)
    private var loadingCards by mutableStateOf(0)
    private var selectedSets by mutableStateOf(emptySet<TrendCardSet>())

    @get:Composable
    val state: GameState
        get() {
            val cards = this.cards
            val ideasCount = this.ideasCount
            val pageIndex = this.pageIndex
            val loadingState = LoadingState(
                addingIdea = loadingAddIdea != 0,
                gettingCards = loadingCards != 0
            )
            return when {
                !loadedInitialState -> {
                    GameState.Loading
                }
                cards.isEmpty() -> {
                    GameState.ChooseSet(sets, selectedSets, loadingState)
                }
                pageIndex in cards.indices -> {
                    GameState.Details(cards[pageIndex])
                }
                else -> {
                    GameState.Playing(cards, ideasCount, loadingState)
                }
            }
        }

    fun addIdea(text: String) {
        coroutineScope.launch {
            loadingAddIdea += 1
            val ideaResponse = client.addIdea(Idea(text))
            ideasCount = ideaResponse.ideasCount
            loadingAddIdea -= 1
        }
    }

    fun loadState() = coroutineScope.launch {
        val ideasCountDeferred = async { client.getIdeaCount().ideasCount }
        val cardsDeferred = async { client.getCards() }
        val setsDeferred = async { client.getSets() }
        ideasCount = ideasCountDeferred.await()
        cards = cardsDeferred.await()
        sets = setsDeferred.await()
        loadedInitialState = true
        webApp.expand()
    }

    fun goNext() {
        pageIndex += 1
    }

    fun generateCards() {
        coroutineScope.launch {
            loadingCards += 1
            cards = client.generateCards()
            loadingCards -= 1
        }
    }

    fun selectSet(set: TrendCardSet) {
        selectedSets = selectedSets + set
    }

    fun unselectSet(set: TrendCardSet) {
        selectedSets = selectedSets - set
    }

    fun resetCards() {
        pageIndex = 0
        cards = emptyList()
    }

    fun finish() {
        coroutineScope.launch {
            client.finish()
            webApp.close()
        }
    }
}
