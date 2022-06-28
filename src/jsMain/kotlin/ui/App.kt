package ui

import GameState
import androidx.compose.runtime.*
import dev.inmo.tgbotapi.webapps.webApp
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.*
import trends.TrendCard

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
            is GameState.Details -> DetailsScreen(tempState.card) { screenModel.goNext() }
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
private fun DetailsScreen(card: TrendCard, goNext: () -> Unit) {
    Div(attrs = {
        classes("card")
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
            P {
                Text(card.description)
            }
        }
    }
    Button(attrs = {
        onClick { goNext() }
        classes("btn", "btn-primary")
        style {
            margin(8.px)
        }
    }) {
        Text("Далее")
    }
}

@Composable
private fun PlayingScreen(state: GameState.Playing) {
    if (false) {
        Div(attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                alignItems(AlignItems.Center)
            }
        }) {
            Button(attrs = {
                onClick {}
                classes("btn")
                style {
                    marginLeft(8.px)
                }
            }) {
                Text("Новые карточки")
            }
            Div(attrs = { style { flex(1) } })
            Button(attrs = {
                onClick {}
                classes("btn")
                style {
                    marginRight(8.px)
                }
            }) {
                Text("Отправить всё")
            }
        }
    }
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
    Div(attrs = {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            alignItems(AlignItems.Center)
        }
    }) {
        var inputState by remember { mutableStateOf("") }
        Input(InputType.Text) {
            value(inputState)
            onInput { event -> inputState = event.value }
            style {
                flex(1)
                margin(8.px)
            }
            placeholder("Опиши свою идею здесь")
            classes("input")
        }
        Button(attrs = {
            onClick {
                inputState = ""
            }
            classes("btn", "btn-primary")
            style {
                margin(8.px)
            }
        }) {
            Text("Добавить")
        }
    }
}
