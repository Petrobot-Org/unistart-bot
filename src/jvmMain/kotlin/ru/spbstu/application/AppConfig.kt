package ru.spbstu.application

import ru.spbstu.application.telegram.TelegramToken
import java.util.*

class AppConfig(
    val telegramToken: TelegramToken,
    val jdbcString: String
)

fun readAppConfig(): AppConfig {
    return AppConfig::class.java.getResourceAsStream(
        "/application.properties"
    ).use {inputStream->
        val properties = Properties().apply { load(inputStream) }
        val jdbcString = properties["jdbc"].toString()
        AppConfig(
            jdbcString = jdbcString,
            telegramToken = TelegramToken(System.getenv("TELEGRAM_TOKEN"))
        )
    }
}
