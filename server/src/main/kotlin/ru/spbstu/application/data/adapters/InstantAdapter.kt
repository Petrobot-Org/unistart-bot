package ru.spbstu.application.data.adapters

import app.cash.sqldelight.ColumnAdapter
import java.time.Instant

object InstantAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant =
        Instant.ofEpochSecond(databaseValue)

    override fun encode(value: Instant): Long =
        value.epochSecond
}
