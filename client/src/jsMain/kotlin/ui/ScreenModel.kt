package ui

import GameClient
import GameState
import LoadingState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.inmo.tgbotapi.webapps.webApp
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trendyfriendy.Idea
import trendyfriendy.TrendCard

class ScreenModel(
    private val client: GameClient
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var cards by mutableStateOf<List<TrendCard>?>(null)
    private var ideasCount by mutableStateOf(0)
    private var pageIndex by mutableStateOf(0)
    private var loadingAddIdea by mutableStateOf(0)

    @get:Composable
    val state: GameState
        get() {
            val cards = this.cards
            val ideasCount = this.ideasCount
            val pageIndex = this.pageIndex
            return if (cards == null) {
                GameState.Loading
            } else if (pageIndex in cards.indices) {
                GameState.Details(cards[pageIndex])
            } else {
                val loadingState = LoadingState(loadingAddIdea != 0)
                GameState.Playing(cards, ideasCount, loadingState)
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

    fun loadCards() = coroutineScope.launch {
        ideasCount = client.getIdeaCount().ideasCount
        cards = listOf(
            TrendCard(
                "Тренд «тёмная тема»",
                "Объективно лучше",
                "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0"
            ),
            TrendCard(
                "Тренд «закруглённые края»",
                "Объективно красивее",
                "https://designmodo.com/wp-content/uploads/2012/06/rectangles.jpg"
            ),
        )
        webApp.expand()
    }

    fun goNext() {
        pageIndex += 1
    }
}
