package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase

class UserRepositoryImpl(private val database: AppDatabase) : UserRepository {
    override fun get(id: User.Id): User? {
        return database.userQueries.get(id).executeAsOneOrNull()?.toDomainModel()
    }

    override fun get(phoneNumber: PhoneNumber): User? {
        return database.userQueries.getByPhoneNumber(phoneNumber).executeAsOneOrNull()?.toDomainModel()
    }

    override fun add(user: User) {
        database.userQueries.add(
            user.id,
            user.phoneNumber,
            user.avatar,
            user.occupation,
            user.availableStepsCount,
            user.amountOfCoins
        )
    }

    override fun contains(phoneNumber: PhoneNumber): Boolean {
        return database.userQueries.containsPhoneNumber(phoneNumber).executeAsOne() >= 1L
    }

    override fun contains(id: User.Id): Boolean {
        return database.userQueries.get(id).executeAsOneOrNull() != null
    }

    override fun sortByAmountOfCoins(): List<User> {
        return database.userQueries.sortByAmountOfCoins().executeAsList().map { it.toDomainModel() }
    }

    override fun setAmountOfCoins(id: User.Id, newAmountOfCoins: Int) {
        database.userQueries.updateAmountOfCoins(newAmountOfCoins, id)
    }

    override fun setAvailableStepsCount(id: User.Id, newAvailableStepsCount: Int) {
        database.userQueries.updateAvailableStepsCount(newAvailableStepsCount, id)
    }

    private fun ru.spbstu.application.data.source.Users.toDomainModel() = User(
        id = id,
        phoneNumber = phoneNumber,
        avatar = avatar,
        occupation = occupation,
        availableStepsCount = availableStepsCount,
        amountOfCoins = amountOfCoins
    )
}
