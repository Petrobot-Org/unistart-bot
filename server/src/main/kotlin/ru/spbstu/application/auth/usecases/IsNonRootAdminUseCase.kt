package ru.spbstu.application.auth.usecases

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.AdminRepository

class IsNonRootAdminUseCase(
    private val adminRepository: AdminRepository
) {
    operator fun invoke(userId: User.Id): Boolean {
        return adminRepository.contains(userId)
    }
}
