package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.Subscription

interface SubscriptionRepository {
    fun add(subscription: Subscription)
}