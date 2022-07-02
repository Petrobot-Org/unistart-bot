package ru.spbstu.application.auth.entities

import kotlin.jvm.JvmInline

data class User(
    val id: Id,
    val phoneNumber: PhoneNumber,
    val avatar: Avatar,
    val occupation: Occupation,
    val availableStepsCount: Long
) {
    @JvmInline
    value class Id(val value: Long)
}
