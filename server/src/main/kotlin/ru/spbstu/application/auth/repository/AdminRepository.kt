package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Admin
import ru.spbstu.application.auth.entities.User

interface AdminRepository {
    fun add(id: User.Id)
    fun delete(id: User.Id)
    fun contains(id: User.Id): Boolean
    fun findAll(): List<Admin>
}
