package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Step
import java.time.Instant

interface StepRepository {
    fun get(id: Step.Id): Step?
//    fun add(user: Step)
    fun changeDuration(id: Step.Id, duration: Instant): Step?
}