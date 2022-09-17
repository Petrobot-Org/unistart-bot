package ru.spbstu.application.data.adapters

import app.cash.sqldelight.ColumnAdapter
import ru.spbstu.application.auth.entities.PhoneNumber

object UserPhoneNumberAdapter : ColumnAdapter<PhoneNumber, String> {
    override fun decode(databaseValue: String): PhoneNumber =
        PhoneNumber.valueOf(databaseValue)!!

    override fun encode(value: PhoneNumber): String =
        value.value
}
