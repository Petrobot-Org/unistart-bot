package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.steps.entities.BonusAccounting

object BonusAccountingIdAdapter : ColumnAdapter<BonusAccounting.Id, Long> {
    override fun decode(databaseValue: Long): BonusAccounting.Id =
       BonusAccounting.Id(databaseValue)

    override fun encode(value: BonusAccounting.Id): Long =
        value.value
}