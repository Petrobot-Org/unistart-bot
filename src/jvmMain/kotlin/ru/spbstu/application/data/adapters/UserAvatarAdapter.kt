package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.auth.entities.Avatar

object UserAvatarAdapter : ColumnAdapter<Avatar, String>{
    override fun decode(databaseValue: String): Avatar {
        return when(databaseValue){
            Avatar.Male.name -> Avatar.Male
            Avatar.DigitalAgile.name -> Avatar.DigitalAgile
            Avatar.Female.name -> Avatar.Female
            else -> TODO("I don't understand, how it should work. Ivan")
        }
    }

    override fun encode(value: Avatar): String =
        value.name
}