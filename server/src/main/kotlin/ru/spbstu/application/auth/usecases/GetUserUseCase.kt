package ru.spbstu.application.auth.usecases

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.entities.users.*
import ru.spbstu.application.auth.repository.UserRepository
import java.time.Instant

class GetUserUseCase(
    private val isRootAdmin: IsRootAdminUseCase,
    private val isNonRootAdmin: IsNonRootAdminUseCase,
    private val isSubscribed: IsSubscribedUseCase,
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: User.Id, at: Instant): BaseUser {
        val user = userRepository.get(userId) ?: return EmptyUser
        return when {
            isRootAdmin(userId) -> RootAdminUser(user)
            isNonRootAdmin(userId) -> AdminUser(user)
            isSubscribed(userId, at) -> SubscribedUser(user)
            else -> ExpiredUser(user)
        }
    }
}
