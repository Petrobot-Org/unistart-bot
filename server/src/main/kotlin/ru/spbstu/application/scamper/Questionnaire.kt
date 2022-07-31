package ru.spbstu.application.scamper

data class Questionnaire(val letters: List<ScamperLetter>)

data class ScamperLetter(
    val character: Char,
    val description: String,
    val questions: List<String>
)
