package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Step

interface StepRepository {
    fun get(id: Step.Id): Step
    fun changeDuration(id: Step.Id, duration: Long): Step
}