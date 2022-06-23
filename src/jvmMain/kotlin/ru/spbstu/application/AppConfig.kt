package ru.spbstu.application

import ru.spbstu.application.telegram.TelegramToken

class AppConfig(
    val telegramToken: TelegramToken
)

fun readAppConfig(): AppConfig {
    return AppConfig(
        telegramToken = TelegramToken(System.getenv("TELEGRAM_TOKEN"))
    )
}
