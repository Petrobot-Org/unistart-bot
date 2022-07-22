package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Admin
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase

class AdminRepositoryImpl(private val database: AppDatabase) : AdminRepository {
    private val mapper = { id: User.Id ->
        Admin(id)
    }

    override fun add(id: User.Id) {
        database.adminQueries.add(id)
    }

    override fun delete(id: User.Id) {
        database.adminQueries.delete(id)
    }

    override fun contains(id: User.Id): Boolean {
        return database.adminQueries.contains(id).executeAsOne() >= 1L
    }

    override fun findAll(): List<Admin> {
        return database.adminQueries.findAll().executeAsList().map(mapper)
    }
}