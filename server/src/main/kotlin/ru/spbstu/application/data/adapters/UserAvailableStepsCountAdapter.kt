package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter

object UserAvailableStepsCountAdapter : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int =
        databaseValue.toInt()
    override fun encode(value: Int): Long =
        value.toLong()
}
