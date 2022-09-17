package ru.spbstu.application.telegram

import java.io.File

val telegramToken by lazy { readToken().filterNot { it.isWhitespace() } }

private fun readToken(): String {
    return System.getenv()["TOKEN"] ?: readFromFile()
}

private fun readFromFile(): String {
    return File(System.getenv("TOKEN_FILE")).readText()
}
