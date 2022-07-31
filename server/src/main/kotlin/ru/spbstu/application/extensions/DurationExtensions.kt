package ru.spbstu.application.extensions

import java.time.Duration

operator fun Duration.times(factor: Double): Duration {
    return Duration.ofSeconds((seconds * factor).toLong())
}
