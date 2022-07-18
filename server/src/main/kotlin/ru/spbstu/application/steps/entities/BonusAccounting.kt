package ru.spbstu.application.steps.entities

import ru.spbstu.application.auth.entities.User

class BonusAccounting(
    val id: Id = Id(0),
    val userId: User.Id,
    val bonusType: BonusType
){
    @JvmInline
    value class Id(val value: Long)
}
