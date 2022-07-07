package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase

class UserRepositoryImpl(private val database: AppDatabase) : UserRepository {
    private val map = { id: User.Id?,
                        phoneNumber: PhoneNumber?,
                        avatar: Avatar,
                        occupation: Occupation,
                        availableStepsCount: Long?,
                        amountOfCoins: Long?->
        User(id!!, phoneNumber!!, avatar, occupation, availableStepsCount!!,amountOfCoins!!)
    }

    override fun get(id: User.Id): User? {
        return database.userQueries.getUserById(id, map).executeAsOneOrNull()
    }

    override fun add(user: User) {
        database.userQueries.addUser(
            user.id,
            user.phoneNumber,
            user.avatar,
            user.occupation,
            user.availableStepsCount,
            user.amountOfCoins
        )
    }

    override fun contains(phoneNumber: PhoneNumber): Boolean {
        return database.userQueries.containsUserByPhoneNumber(phoneNumber).executeAsOne() >= 1L
    }

    override fun getAmountOfCoins(id: User.Id): Long {
        return database.userQueries.getUserById(id,map).executeAsOne().amountOfCoins
    }

    override fun getAll(): List<User> {
        return database.userQueries.getAll(map).executeAsList()
    }
}
