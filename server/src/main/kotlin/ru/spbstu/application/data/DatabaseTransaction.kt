package ru.spbstu.application.data

fun interface DatabaseTransaction {
    operator fun invoke(body: () -> Unit)
}
