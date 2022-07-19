package ru.spbstu.application.steps.repository

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.steps.entities.BonusAccounting
import ru.spbstu.application.steps.entities.BonusType

interface BonusAccountingRepository {
    fun add(bonusAccounting: BonusAccounting)
    fun get(userId: User.Id, bonusType: BonusType): BonusAccounting?
    fun getBonusesByUsedId(userId: User.Id): List<BonusType>
    fun getByUsedId(userId: User.Id): List<BonusAccounting>
}