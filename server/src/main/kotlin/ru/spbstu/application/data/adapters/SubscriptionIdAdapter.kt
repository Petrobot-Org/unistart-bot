package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.auth.entities.Subscription

object SubscriptionIdAdapter : ColumnAdapter<Subscription.Id, Long> {
    override fun decode(databaseValue: Long): Subscription.Id =
        Subscription.Id(databaseValue)

    override fun encode(value: Subscription.Id): Long =
        value.value
}
