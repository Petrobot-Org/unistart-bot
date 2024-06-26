package ru.spbstu.application.data

interface DatabaseTransaction {
    operator fun invoke(body: () -> Unit)
}

interface DatabaseTransactionWithResult {
    operator fun <R> invoke(body: () -> R): R
}
