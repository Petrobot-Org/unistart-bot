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
    private val ideasFlow = MutableStateFlow(emptyList<String>())
    val state = combine(cardsFlow, ideasFlow) { cards, ideas ->
        if (cards == null) {
            GameState.Loading
        } else {
            GameState.Playing(cards, ideas)
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000L), GameState.Loading)

    fun addIdea(text: String) {
        ideasFlow.value += text
    }

    fun loadCards() = coroutineScope.launch {
        delay(1000)
        cardsFlow.value = listOf(
            TrendCard("Тренд 1", "de", "https://polikek.ithersta.com/static/cards/pochernin.webp"),
            TrendCard("Тренд 2", "de", "https://polikek.ithersta.com/static/cards/pochernin2.webp"),
            TrendCard("Тренд 3", "de", "https://polikek.ithersta.com/static/cards/chad.webp")
        )
        webApp.expand()
    }
}
