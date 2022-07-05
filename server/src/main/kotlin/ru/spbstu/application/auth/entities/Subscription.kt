package ru.spbstu.application.auth.entities

import java.time.Instant

class Subscription(
    val id: Id, val start: Instant, val duration: Instant, val user: User
) {
    @JvmInline
    value class Id(val value: Long)
}