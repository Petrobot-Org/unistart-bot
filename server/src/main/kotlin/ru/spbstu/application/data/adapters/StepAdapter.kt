package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.steps.entities.Step

object StepAdapter : ColumnAdapter<Step, Long> {
    override fun decode(databaseValue: Long): Step =
        Step(databaseValue)

    override fun encode(value: Step): Long =
        value.value
}
