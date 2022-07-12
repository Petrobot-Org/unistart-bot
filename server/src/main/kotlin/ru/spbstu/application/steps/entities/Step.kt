package ru.spbstu.application.steps.entities

@JvmInline
value class Step(val value: Long) {
    init {
        require(value in 0..4)
    }
}
