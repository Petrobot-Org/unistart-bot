package ru.spbstu.application.steps.repository

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.steps.entities.BonusAccounting
import ru.spbstu.application.steps.entities.BonusType

interface BonusAccountingRepository {
    fun add(bonusAccounting: BonusAccounting)
    fun get(userId: User.Id, bonusType: BonusType): BonusAccounting?
    fun getBonusesByUserId(userId: User.Id): List<BonusType>
}
