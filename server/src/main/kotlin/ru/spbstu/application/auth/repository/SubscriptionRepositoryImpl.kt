package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Subscription
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase
import java.time.Duration
import java.time.Instant

class SubscriptionRepositoryImpl(private val database: AppDatabase) : SubscriptionRepository {
    private val mapper = { id: Subscription.Id, start: Instant, duration: Duration, user: User.Id ->
        Subscription(id, start, duration, user)
    }

    override fun add(subscription: Subscription) {
        database.subscriptionQueries.add(
            subscription.id, subscription.start, subscription.duration, subscription.userId
        )
    }

    override fun get(userId: User.Id): List<Subscription> {
        return database.subscriptionQueries.get(userId, mapper).executeAsList()
    }
}
