package ru.spbstu.application.auth.entities

import java.time.Duration
import java.time.Instant

class StartInfo(
    val phoneNumber: PhoneNumber,
    val begin: Instant,
    val duration: Duration,
    val id: Id = Id(0)
) {
    @JvmInline
    value class Id(val value: Long)
}
