package ru.spbstu.application.auth.entities

import java.time.Instant

class StartInfo (
    val id: Id,
    val number: PhoneNumber,
    val begin: Instant,
    val end: Instant
)
     {
        @JvmInline
        value class Id(val value: Long)
    }