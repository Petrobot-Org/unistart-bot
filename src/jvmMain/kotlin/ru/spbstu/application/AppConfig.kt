package ru.spbstu.application

import ru.spbstu.application.telegram.TelegramToken

class AppConfig(
    val telegramToken: TelegramToken
)

fun readAppConfig(): AppConfig {
    return AppConfig(
//        telegramToken = TelegramToken(System.getenv("TELEGRAM_TOKEN"))
        // временно на тест
        telegramToken = TelegramToken("5210213874:AAEtGKJg8qagNH4I0uQiegYHBUkFw1m-MPg")
    )
}
