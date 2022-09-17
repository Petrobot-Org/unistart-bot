package ru.spbstu.application.auth.entities

import kotlinx.serialization.Serializable

data class User(
    override val id: Id,
    override val phoneNumber: PhoneNumber,
    override val avatar: Avatar,
    override val occupation: Occupation,
    override val availableStepsCount: Int,
    override val amountOfCoins: Int
) : UserData {
    @Serializable
    @JvmInline
    value class Id(val value: Long)
}

interface UserData {
    val id: User.Id
    val phoneNumber: PhoneNumber
    val avatar: Avatar
    val occupation: Occupation
    val availableStepsCount: Int
    val amountOfCoins: Int
}
