package ui

import GameState
import androidx.compose.runtime.*
import dev.inmo.tgbotapi.webapps.webApp
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.*

@Composable
fun App(screenModel: ScreenModel) {
    val state by screenModel.state.collectAsState()
    DisposableEffect(Unit) {
        screenModel.loadCards()
        webApp.ready()
        onDispose { }
    }
    Div(attrs = {
        classes("container", "mx-auto")
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            height(100.vh)
        }
    }) {
        when (val tempState = state) {
            GameState.Loading -> LoadingScreen()
            is GameState.Playing -> PlayingScreen(tempState)
        }
    }
}

@Composable
private fun LoadingScreen() {
    H6(attrs = {
        style {
            textAlign("center")
            margin(32.px)
        }
    }) {
        Text("Загружаю карточки...")
    }
}

@Composable
private fun PlayingScreen(state: GameState.Playing) {
    for (card in state.cards) {
        Div(attrs = {
            classes("card", "image-full")
            style {
                flex(1)
                margin(8.px)
            }
        }) {
            Img(attrs = { classes("card-image") }, src = card.url)
            Div(attrs = {
                classes("card-body")
            }) {
                H2(attrs = {
                    classes("card-title")
                }) {
                    Text(card.name)
                }
            }
        }
    }
    Button(attrs = {
        onClick {  }
        classes("btn", "btn-primary")
        style {
            margin(8.px)
        }
    }) {
        Text("Добавить идею")
    }
}
