package ui

import GameClient
import GameState
import dev.inmo.tgbotapi.webapps.webApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import trends.TrendCard

class ScreenModel(
    private val client: GameClient
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val cardsFlow = MutableStateFlow<List<TrendCard>?>(null)
    private val ideasCountFlow = MutableStateFlow(0)
    private val detailsPageFlow = MutableStateFlow(0)
    val state = combine(cardsFlow, ideasCountFlow, detailsPageFlow) { cards, ideasCount, detailsPage ->
        if (cards == null) {
            GameState.Loading
        } else if (detailsPage in cards.indices) {
            GameState.Details(cards[detailsPage])
        } else {
            GameState.Playing(cards, ideasCount)
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000L), GameState.Loading)

    fun addIdea(text: String) {
        ideasCountFlow.value += 1
    }

    fun loadCards() = coroutineScope.launch {
        delay(1000)
        cardsFlow.value = listOf(
            TrendCard("Тренд «тёмная тема»", "Объективно лучше", "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0"),
            TrendCard("Тренд «закруглённые края»", "Объективно красивее", "https://designmodo.com/wp-content/uploads/2012/06/rectangles.jpg"),
        )
        webApp.expand()
    }

    fun goNext() {
        detailsPageFlow.value += 1
    }
}
