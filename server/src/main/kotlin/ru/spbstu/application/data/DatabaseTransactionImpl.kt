package ru.spbstu.application.data

import ru.spbstu.application.data.source.AppDatabase

class DatabaseTransactionImpl(private val appDatabase: AppDatabase) : DatabaseTransaction {
    override fun invoke(body: () -> Unit) {
        appDatabase.transaction { body() }
    }
}

class DatabaseTransactionWithResultImpl(private val appDatabase: AppDatabase) : DatabaseTransactionWithResult {
    override fun <R> invoke(body: () -> R): R {
        return appDatabase.transactionWithResult { body() }
    }
}
