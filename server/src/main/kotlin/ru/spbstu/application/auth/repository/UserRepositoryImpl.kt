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
                        availableStepsCount: Long? ->
        User(id!!, phoneNumber!!, avatar, occupation, availableStepsCount!!)
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
            user.availableStepsCount
        )
    }

    override fun contains(phoneNumber: PhoneNumber): Boolean {
        return database.userQueries.containsUserByPhoneNumber(phoneNumber).executeAsOne() >= 1L
    }
}
