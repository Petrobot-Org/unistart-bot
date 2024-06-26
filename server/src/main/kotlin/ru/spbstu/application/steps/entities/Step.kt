package ru.spbstu.application.steps.entities

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Step(val value: Int) {
    init {
        require(value in 0..LastValue)
    }

    companion object {
        const val LastValue = 4
    }
}
