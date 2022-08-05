package ru.spbstu.application.configuration

import dev.inmo.tgbotapi.utils.TelegramAPIUrlsKeeper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.usecases.IsSubscribedUseCase
import ru.spbstu.application.telegram.TelegramToken
import java.time.Instant

@Serializable
class WebAppUser(
    val id: Long,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    val username: String? = null,
    @SerialName("language_code") val languageCode: String? = null
)

fun Application.configureAuthentication() {
    val isSubscribed: IsSubscribedUseCase by inject()
    val telegramToken: TelegramToken by inject()
    val telegramApiUrlsKeeper = TelegramAPIUrlsKeeper(
        telegramToken.value
    )
    install(Authentication) {
        basic {
            validate { credentials ->
                val data = credentials.name
                val hash = credentials.password
                if (telegramApiUrlsKeeper.checkWebAppData(data, hash)) {
                    val userData = data
                        .decodeURLQueryComponent()
                        .split("&")
                        .first { it.startsWith("user=") }
                        .removePrefix("user=")
                    val webAppUser = Json.decodeFromString<WebAppUser>(userData)
                    if (isSubscribed(User.Id(webAppUser.id), Instant.now())) {
                        UserIdPrincipal(webAppUser.id.toString())
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }
    }
}