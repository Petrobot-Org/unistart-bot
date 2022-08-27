package ru.spbstu.application.auth.usecases

import ru.spbstu.application.AppConfig
import ru.spbstu.application.auth.entities.User

class IsRootAdminUseCase(
    private val appConfig: AppConfig
) {
    operator fun invoke(userId: User.Id): Boolean {
        return userId in appConfig.rootAdminUserIds
    }
}
