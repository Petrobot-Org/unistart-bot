package ru.spbstu.application.auth.entities

import java.time.Duration
import java.time.Instant

class Subscription(
    val id: Id,
    val start: Instant,
    val duration: Duration,
    val userId: User.Id
) {
    @JvmInline
    value class Id(val value: Long)
}