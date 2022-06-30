package ru.spbstu.application

import ru.spbstu.application.telegram.TelegramToken
import java.util.*

class AppConfig(
    val telegramToken: TelegramToken,
    val jdbcString: String,
    val publicHostname: String
)

fun readAppConfig(): AppConfig {
    return AppConfig::class.java.getResourceAsStream(
        "/application.properties"
    ).use {inputStream->
        val properties = Properties().apply { load(inputStream) }
        val jdbcString = properties["jdbc"].toString()
        val environmentVariables = System.getenv()
        AppConfig(
            jdbcString = jdbcString,
            telegramToken = TelegramToken(environmentVariables.getValue("TELEGRAM_TOKEN")),
            publicHostname = environmentVariables["PUBLIC_HOSTNAME"] ?: "https://127.0.0.1"
        )
    }
}
