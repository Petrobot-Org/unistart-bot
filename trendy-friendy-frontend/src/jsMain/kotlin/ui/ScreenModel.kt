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
import org.w3c.dom.Image
import trendyfriendy.Idea
import trendyfriendy.TrendCard
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ScreenModel(
    private val client: GameClient
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var sets = emptySet<String>()
    private var loadedInitialState by mutableStateOf(false)
    private var cards by mutableStateOf<List<TrendCard>>(emptyList())
    private var ideasCount by mutableStateOf(0)
    private var pageIndex by mutableStateOf(0)
    private var loadingAddIdea by mutableStateOf(0)
    private var loadingCards by mutableStateOf(0)
    private var loadingFinish by mutableStateOf(0)
    private var selectedSets by mutableStateOf(emptySet<String>())
    private var ideaInput by mutableStateOf("")

    @get:Composable
    val state: GameState
        get() {
            val cards = this.cards
            val ideasCount = this.ideasCount
            val pageIndex = this.pageIndex
            val loadingState = LoadingState(
                addingIdea = loadingAddIdea != 0,
                gettingCards = loadingCards != 0,
                finishing = loadingFinish != 0
            )
            return when {
                !loadedInitialState -> {
                    GameState.Loading
                }
                cards.isEmpty() -> {
                    GameState.ChooseSet(sets, selectedSets, loadingState)
                }
                pageIndex in cards.indices -> {
                    GameState.Details(cards[pageIndex], loadingState)
                }
                else -> {
                    GameState.Playing(cards, ideasCount, ideaInput, loadingState)
                }
            }
        }

    fun onIdeaInput(text: String) {
        ideaInput = text
    }

    fun addIdea() {
        if (ideaInput.isBlank()) {
            return
        }
        val idea = Idea(ideaInput)
        ideaInput = ""
        coroutineScope.launch {
            loadingAddIdea += 1
            val ideaResponse = client.addIdea(idea)
            ideasCount = ideaResponse.ideasCount
            loadingAddIdea -= 1
        }
    }

    fun loadState() = coroutineScope.launch {
        val ideasCountDeferred = async { client.getIdeaCount().ideasCount }
        val cardsDeferred = async { client.getCards() }
        val setsDeferred = async { client.getSets() }
        cards = cardsDeferred.await()
        cards.map { async { preloadImage(it.url) } }.forEach { it.await() }
        ideasCount = ideasCountDeferred.await()
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
            cards = client.generateCards(selectedSets)
            cards.map { async { preloadImage(it.url) } }.forEach { it.await() }
            loadingCards -= 1
        }
    }

    fun selectSet(set: String) {
        selectedSets = selectedSets + set
    }

    fun unselectSet(set: String) {
        selectedSets = selectedSets - set
    }

    fun selectAllSets() {
        selectedSets = sets
    }

    fun unselectAllSets() {
        selectedSets = emptySet()
    }

    fun resetCards() {
        pageIndex = 0
        cards = emptyList()
    }

    fun finish() {
        coroutineScope.launch {
            loadingFinish += 1
            client.finish()
            webApp.close()
        }
    }
}

private suspend fun preloadImage(url: String) {
    suspendCoroutine<Unit> { continuation ->
        val image = Image()
        image.src = url
        image.onload = {
            continuation.resume(Unit)
        }
        image.onerror = { _, _, _, _, _ ->
            continuation.resumeWithException(Exception())
        }
    }
}
