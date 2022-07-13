package ru.spbstu.application.data

import ru.spbstu.application.data.source.AppDatabase

class DatabaseTransactionImpl(private val appDatabase: AppDatabase) : DatabaseTransaction {
    override fun invoke(body: () -> Unit) {
        appDatabase.transaction { body() }
    }
}
