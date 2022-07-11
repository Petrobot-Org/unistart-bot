package ru.spbstu.application.auth.entities

@JvmInline
value class PhoneNumber private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): PhoneNumber? {
            return if (value.all { it.isDigit() }) {
                PhoneNumber(value)
            } else {
                null
            }
        }
    }
}
