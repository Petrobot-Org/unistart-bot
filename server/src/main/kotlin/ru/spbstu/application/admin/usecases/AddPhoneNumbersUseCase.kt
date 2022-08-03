package ru.spbstu.application.admin.usecases

import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.StartInfo
import ru.spbstu.application.auth.repository.StartInfoRepository
import ru.spbstu.application.auth.repository.SubscriptionRepository
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransactionWithResult
import java.time.Duration
import java.time.Instant

class AddPhoneNumbersUseCase(
    private val startInfoRepository: StartInfoRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository,
    private val transactionWithResult: DatabaseTransactionWithResult
) {
    operator fun invoke(phoneNumbers: Set<PhoneNumber>, start: Instant, duration: Duration) = transactionWithResult {
        val allChanges = phoneNumbers.sumOf {
            val user = userRepository.get(it)
            if (user == null) {
                startInfoRepository.add(StartInfo(it, start, duration))
            } else {
                subscriptionRepository.add(start, duration, user.id)
            }
            transactionWithResult.changes()
        }
        return@transactionWithResult allChanges
    }
}
