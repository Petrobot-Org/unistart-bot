package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Subscription
import ru.spbstu.application.auth.entities.User

interface SubscriptionRepository {
    fun add(subscription: Subscription)
    fun get(userId: User.Id): List<Subscription>
}
