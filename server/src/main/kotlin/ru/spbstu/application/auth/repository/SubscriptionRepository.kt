package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Subscription
import ru.spbstu.application.auth.entities.User
import java.time.Duration
import java.time.Instant

interface SubscriptionRepository {
    fun add(start: Instant, duration: Duration, userId: User.Id): Boolean
    fun get(userId: User.Id): List<Subscription>
}
