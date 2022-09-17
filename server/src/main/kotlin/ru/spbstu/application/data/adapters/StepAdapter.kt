package ru.spbstu.application.data.adapters

import app.cash.sqldelight.ColumnAdapter
import ru.spbstu.application.steps.entities.Step

object StepAdapter : ColumnAdapter<Step, Int> {
    override fun decode(databaseValue: Int): Step =
        Step(databaseValue)

    override fun encode(value: Step): Int =
        value.value
}
