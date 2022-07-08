package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import java.time.Instant

object StepStartAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant =
        Instant.ofEpochSecond(databaseValue)

    override fun encode(value: Instant): Long =
        value.epochSecond
}