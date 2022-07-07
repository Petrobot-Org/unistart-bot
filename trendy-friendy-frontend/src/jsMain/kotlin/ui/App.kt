package ui

import GameState
import androidx.compose.runtime.*
import dev.inmo.tgbotapi.webapps.webApp
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import trendyfriendy.TrendCard

@Composable
fun App(screenModel: ScreenModel) {
    val state = screenModel.state
    DisposableEffect(Unit) {
        screenModel.loadState()
        webApp.ready()
        onDispose { }
    }
    Div(attrs = {
        classes(buildList {
            add("loadable")
            if (state.loadingState.finishing) add("loading")
        })

        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            height(100.vh)
        }
    }) {
        when (state) {
            GameState.Loading -> LoadingScreen()
            is GameState.ChooseSet -> ChooseSetScreen(
                state = state,
                generateCards = { screenModel.generateCards() },
                select = { screenModel.selectSet(it) },
                unselect = { screenModel.unselectSet(it) },
                selectAll = { screenModel.selectAllSets() },
                unselectAll = { screenModel.unselectAllSets() }
            )
            is GameState.Details -> DetailsScreen(state.card) { screenModel.goNext() }
            is GameState.Playing -> PlayingScreen(
                state = state,
                addIdea = { screenModel.addIdea(it) },
                resetCards = { screenModel.resetCards() },
                finish = { screenModel.finish() }
            )
        }
    }
}

@Composable
private fun ChooseSetScreen(
    state: GameState.ChooseSet,
    generateCards: () -> Unit,
    select: (String) -> Unit,
    unselect: (String) -> Unit,
    selectAll: () -> Unit,
    unselectAll: () -> Unit
) {
    Div(attrs = {
        style {
            flex(1)
        }
    })
    Div(attrs = {
        style {
            marginLeft(32.px)
            marginRight(32.px)
            marginTop((-32).px)
        }
    }) {
        LabeledCheckbox(
            text = {
                H6 {
                    Text("Категории трендов")
                }
            },
            checked = state.sets.size == state.selectedSets.size,
            select = selectAll,
            unselect = unselectAll
        )
        Div(attrs = {
            classes("divider")
        })
        state.sets.forEach { set ->
            val checked = set in state.selectedSets
            LabeledCheckbox(
                text = {
                    Span(attrs = {
                        classes("label-text")
                    }) {
                        Text(set)
                    }
                },
                checked = checked,
                select = { select(set) },
                unselect = { unselect(set) }
            )
        }
    }
    Div(attrs = {
        style {
            flex(1)
        }
    })
    Button(attrs = {
        onClick { generateCards() }
        classes(buildList {
            add("btn")
            add("btn-primary")
            add("loadable")
            if (state.loadingState.gettingCards) add("loading")
        })
        style {
            margin(8.px)
        }
    }) {
        Text("Далее")
    }
}

@Composable
private fun LabeledCheckbox(
    text: @Composable () -> Unit,
    checked: Boolean,
    select: () -> Unit,
    unselect: () -> Unit
) {
    Div(attrs = {
        classes("form-control")
        onClick { if (checked) unselect() else select() }
    }) {
        Label(attrs = {
            classes("label", "cursor-pointer")
        }) {

            text()
            Input(InputType.Checkbox) {
                classes("checkbox")
                checked(checked)
            }
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
private fun PlayingScreen(
    state: GameState.Playing,
    addIdea: (String) -> Unit,
    resetCards: () -> Unit,
    finish: () -> Unit
) {
    Div(attrs = {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            alignItems(AlignItems.Center)
            marginTop(8.px)
        }
    }) {
        Button(attrs = {
            onClick { resetCards() }
            classes("btn")
            style {
                marginLeft(8.px)
            }
        }) {
            Text("Новые карточки")
        }
        Div(attrs = { style { flex(1) } })
        Button(attrs = {
            onClick { finish() }
            classes("btn")
            if (state.ideasCount < 1) {
                classes("disabled")
            }
            style {
                marginRight(8.px)
            }
        }) {
            Text("Отправить всё (${state.ideasCount})")
        }
    }
    state.cards.forEachIndexed { index, card ->
        Div(attrs = {
            classes("card", "image-full", "delay${index}")
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
    Form(attrs = {
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
        Input(InputType.Submit) {
            onClick {
                addIdea(inputState)
                inputState = ""
            }
            classes("btn", "btn-primary", "loadable")
            if (state.loadingState.addingIdea) {
                classes("loading")
            }
            style {
                marginRight(8.px)
            }
            value("Добавить")
        }
    }
}
