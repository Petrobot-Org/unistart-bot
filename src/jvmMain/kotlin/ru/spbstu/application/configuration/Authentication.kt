package ru.spbstu.application.configuration

import dev.inmo.tgbotapi.utils.TelegramAPIUrlsKeeper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.koin.ktor.ext.inject
import ru.spbstu.application.telegram.TelegramToken

fun Application.configureAuthentication() {
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
                    UserIdPrincipal(data
                        .decodeURLQueryComponent()
                        .split("&")
                        .first { it.startsWith("user") }
                    )
                } else {
                    null
                }
            }
        }
    }
}
