package ru.spbstu.application.auth.entities

import java.time.Instant

data class Step(
    val id: Id, val start: Instant, val duration: Instant
) {
    @JvmInline
    value class Id(val value: Long)
}