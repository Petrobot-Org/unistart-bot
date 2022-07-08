package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.auth.entities.Step

object StepIdAdapter : ColumnAdapter<Step.Id, Long> {
    override fun decode(databaseValue: Long): Step.Id =
        Step.Id(databaseValue)

    override fun encode(value: Step.Id): Long =
        value.value
}