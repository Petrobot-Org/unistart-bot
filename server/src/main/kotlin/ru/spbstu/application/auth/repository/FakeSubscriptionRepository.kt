package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Subscription
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.data.source.AppDatabase
import java.time.Instant

class FakeSubscriptionRepository(private val database: AppDatabase) : SubscriptionRepository {
    private val map = { id: Subscription.Id, start: Instant, duration: Instant, user: User ->
        Subscription(id, start, duration, user)
    }

//    override fun get(id: Subscription.Id): Subscription? {
//    }

    override fun add(subscription: Subscription) {
        database.subscriptionQueries.addSubscription(
            subscription.id, subscription.start, subscription.duration, subscription.user
        )
    }
}
