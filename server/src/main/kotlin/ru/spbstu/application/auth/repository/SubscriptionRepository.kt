package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Subscription

interface SubscriptionRepository {
//    fun get(id: Subscription.Id): Subscription?
    fun add(subscription: Subscription)
}