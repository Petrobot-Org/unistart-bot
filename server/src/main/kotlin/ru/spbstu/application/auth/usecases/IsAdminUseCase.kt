package ru.spbstu.application.auth.usecases

import ru.spbstu.application.auth.entities.User

class IsAdminUseCase(
    private val isNonRootAdminUseCase: IsNonRootAdminUseCase,
    private val isRootAdminUseCase: IsRootAdminUseCase
) {
    operator fun invoke(userId: User.Id): Boolean {
        return isNonRootAdminUseCase(userId) || isRootAdminUseCase(userId)
    }
}
