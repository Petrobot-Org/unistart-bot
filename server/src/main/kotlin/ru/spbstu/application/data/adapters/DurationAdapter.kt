package ru.spbstu.application.data.adapters

import app.cash.sqldelight.ColumnAdapter
import java.time.Duration

object DurationAdapter : ColumnAdapter<Duration, Long> {
    override fun decode(databaseValue: Long): Duration =
        Duration.ofSeconds(databaseValue)

    override fun encode(value: Duration): Long =
        value.seconds
}
