package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.StartInfo

interface StartInfoRepository {
    fun add(startInfo: StartInfo): Boolean
    fun contains(number: PhoneNumber): Boolean
    fun getByPhoneNumber(phoneNumber: PhoneNumber): StartInfo?
}
