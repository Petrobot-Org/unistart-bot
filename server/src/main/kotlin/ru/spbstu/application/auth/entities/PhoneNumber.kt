package ru.spbstu.application.auth.entities

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PhoneNumber private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): PhoneNumber? {
            return if (value.matches(Regex("\\d{2,15}"))) {
                PhoneNumber(value)
            } else {
                null
            }
        }
    }

    override fun toString(): String {
        return value
    }

    fun isRussian(): Boolean {
        return Regex("7\\d{10}").matches(value)
    }
}
