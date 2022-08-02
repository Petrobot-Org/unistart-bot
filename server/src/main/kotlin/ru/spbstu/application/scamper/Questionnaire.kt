package ru.spbstu.application.scamper

import ru.spbstu.application.steps.entities.BonusType

data class Questionnaire(val letters: List<ScamperLetter>)

data class ScamperLetter(
    val character: Char,
    val description: String,
    val questions: List<String>,
    val bonusType: BonusType
)
