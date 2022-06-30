package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase

class UserRepositoryImpl(private val database: AppDatabase) : UserRepository {
    private val map = {id: Long,
        phoneNumber: PhoneNumber?,
        avatar: Avatar,
        occupation: Occupation,
        availableStepsCount: Long? ->
        val user = User(User.Id(id), phoneNumber!!, avatar, occupation, availableStepsCount!!)
    }

    override fun get(id: User.Id): User? {
        return database.userQueries.getUserById(id.value, map);
    }

    override fun add(user: User) {
        database.userQueries.addUser(user.id.value, user.phoneNumber, user.avatar, user.occupation, user.availableStepsCount)
    }

    override fun contains(phoneNumber: PhoneNumber): Boolean {
        return database.userQueries.containsUserByPhoneNumber(phoneNumber).executeAsOne() >= 1L
    }
}