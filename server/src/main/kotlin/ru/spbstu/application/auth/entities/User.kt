package ru.spbstu.application.auth.entities

import kotlinx.serialization.Serializable

data class User(
    val id: Id,
    val phoneNumber: PhoneNumber,
    val avatar: Avatar,
    val occupation: Occupation,
    var availableStepsCount: Long,
    var amountOfCoins: Long
) {
    @Serializable
    @JvmInline
    value class Id(val value: Long)
}
