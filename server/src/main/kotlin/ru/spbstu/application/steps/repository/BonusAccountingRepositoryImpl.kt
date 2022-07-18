package ru.spbstu.application.steps.repository

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase
import ru.spbstu.application.steps.entities.BonusAccounting
import ru.spbstu.application.steps.entities.BonusType

class BonusAccountingRepositoryImpl(private val database: AppDatabase): BonusAccountingRepository{
    private val mapper = { id: BonusAccounting.Id, userId: User.Id, bonusType: BonusType->
        BonusAccounting(id, userId, bonusType)
    }
    override fun add(bonusAccounting: BonusAccounting) {
        database.bonusAccountingQueries.add(
            bonusAccounting.userId,
            bonusAccounting.bonusType)
    }

    override fun get(userId: User.Id, bonusType: BonusType): BonusAccounting? {
       return database.bonusAccountingQueries.get(userId,bonusType, mapper).executeAsOneOrNull()
    }

    override fun getByUsedId(userId: User.Id): List<BonusAccounting> {
        return database.bonusAccountingQueries.getByUserId(userId, mapper).executeAsList()
    }
}