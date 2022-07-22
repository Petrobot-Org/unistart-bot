package ru.spbstu.application.admin.usecases

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.AdminRepository

class IsAdminUseCase(
    private val isRootAdmin: IsRootAdminUseCase,
    private val adminRepository: AdminRepository
) {
    operator fun invoke(userId: User.Id): Boolean {
        return isRootAdmin(userId) || adminRepository.contains(userId)
    }
}
