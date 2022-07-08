package ru.spbstu.application.auth.entities

@JvmInline
value class PhoneNumber private constructor(val value: String) {
    companion object {
        fun valueOf(value: String): PhoneNumber? {
            val reg = Regex("\\+\\d+")
            return if (reg.matches(value)) {
                PhoneNumber(value)
            } else {
                null
            }
        }
    }
}
