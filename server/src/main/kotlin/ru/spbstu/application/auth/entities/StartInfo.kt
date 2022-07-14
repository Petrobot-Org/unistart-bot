package ru.spbstu.application.auth.entities

import java.time.Duration
import java.time.Instant

class StartInfo (
    val id: Id,
    val phoneNumber: PhoneNumber,
    val begin: Instant,
    val duration: Duration
)
     {
        @JvmInline
        value class Id(val value: Long)
    }