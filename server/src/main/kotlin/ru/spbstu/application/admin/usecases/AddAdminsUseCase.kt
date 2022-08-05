package ru.spbstu.application.admin.usecases

import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.repository.AdminRepository
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.DatabaseTransactionWithResult

class AddAdminsUseCase(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository,
    private val transactionWithResult: DatabaseTransactionWithResult
) {
    operator fun invoke(phoneNumbers: Set<PhoneNumber>) = transactionWithResult {
        val errNumbers = mutableListOf<PhoneNumber>()
        phoneNumbers.forEach {
            val user = userRepository.get(it)
            if (user != null) {
                adminRepository.add(user.id)
            } else {
                errNumbers.add(it)
            }
        }
        return@transactionWithResult errNumbers
    }
}
