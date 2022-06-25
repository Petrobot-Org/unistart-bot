package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User

class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()

    override fun get(id: User.Id): User? {
        return users.find { it.id == id }
    }

    override fun add(user: User) {
        users.add(user)
    }

    override fun contains(phoneNumber: PhoneNumber): Boolean {
        return users.any { it.phoneNumber == phoneNumber }
    }
}
