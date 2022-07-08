import dev.inmo.tgbotapi.webapps.webApp
import org.jetbrains.compose.web.renderComposable
import ui.App
import ui.ScreenModel

fun main() {
    val client = GameClient(webApp.initData, webApp.initDataUnsafe.hash)
    val screenModel = ScreenModel(client)
    renderComposable(rootElementId = "root") {
        App(screenModel)
    }
}
