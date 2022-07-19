package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.auth.entities.StartInfo

object StartInfoIdAdapter : ColumnAdapter<StartInfo.Id, Long> {
    override fun decode(databaseValue: Long): StartInfo.Id =
        StartInfo.Id(databaseValue)

    override fun encode(value: StartInfo.Id): Long =
        value.value
}
