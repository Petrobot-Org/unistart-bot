package ru.spbstu.application.auth.entities

data class User(
    val id: Id,
    val phoneNumber: PhoneNumber,
    val avatar: Avatar,
    val occupation: Occupation,
    val availableStepsCount: Int
) {
    @JvmInline
    value class Id(val value: Long)
}