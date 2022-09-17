package ru.spbstu.application.auth.usecases

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.repository.SubscriptionRepository
import java.time.Instant

class IsSubscribedUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {
    operator fun invoke(userId: User.Id, at: Instant): Boolean {
        val subscriptions = subscriptionRepository.get(userId)
        return subscriptions.any {
            at.isAfter(it.start) && at.isBefore(it.start + it.duration)
        }
    }
}
