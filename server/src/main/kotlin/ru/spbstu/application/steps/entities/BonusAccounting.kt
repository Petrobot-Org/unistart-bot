package ru.spbstu.application.steps.entities

import ru.spbstu.application.auth.entities.User

class BonusAccounting(
    val userId: User.Id,
    val bonusType: BonusType
)
