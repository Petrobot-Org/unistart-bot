package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User

interface UserRepository {
    fun get(id: User.Id): User?
    fun get(phoneNumber: PhoneNumber): User?
    fun add(user: User)
    fun contains(phoneNumber: PhoneNumber): Boolean
    fun contains(id: User.Id): Boolean
    fun sortByAmountOfCoins(): List<User>
    fun setAmountOfCoins(id: User.Id, newAmountOfCoins: Long)
    fun setAvailableStepsCount(id: User.Id, newAvailableStepsCount: Long)
}
