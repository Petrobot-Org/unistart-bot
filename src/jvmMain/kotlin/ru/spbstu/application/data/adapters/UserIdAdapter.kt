package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.auth.entities.User

object UserIdAdapter : ColumnAdapter<User.Id, Long> {
    override fun decode(databaseValue: Long): User.Id =
        User.Id(databaseValue)

    override fun encode(value: User.Id): Long =
        value.value
}