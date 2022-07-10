package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.auth.entities.StepTimeFrame

object StepIdAdapter : ColumnAdapter<StepTimeFrame.Id, Long> {
    override fun decode(databaseValue: Long): StepTimeFrame.Id =
        StepTimeFrame.Id(databaseValue)

    override fun encode(value: StepTimeFrame.Id): Long =
        value.value
}
