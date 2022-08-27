package ru.spbstu.application.auth.entities

import kotlinx.serialization.Serializable
import ru.spbstu.application.auth.entities.users.BaseUser

data class User(
    override val id: Id,
    override val phoneNumber: PhoneNumber,
    override val avatar: Avatar,
    override val occupation: Occupation,
    override val availableStepsCount: Long,
    override val amountOfCoins: Long
): UserData {
    @Serializable
    @JvmInline
    value class Id(val value: Long)
}

interface UserData {
    val id: User.Id
    val phoneNumber: PhoneNumber
    val avatar: Avatar
    val occupation: Occupation
    val availableStepsCount: Long
    val amountOfCoins: Long
}
