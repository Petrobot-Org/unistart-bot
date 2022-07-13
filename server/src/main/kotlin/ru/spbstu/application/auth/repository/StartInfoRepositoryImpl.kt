package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.StartInfo
import ru.spbstu.application.data.source.AppDatabase
import java.time.Duration
import java.time.Instant

private val mapper = { id: StartInfo.Id, number: PhoneNumber?, begin: Instant, duration: Duration ->
    StartInfo(id, number!!, begin, duration)
}

class StartInfoRepositoryImpl(private val database: AppDatabase) : StartInfoRepository {
    override fun add(startInfo: StartInfo) {
        database.startInfoQueries.add(
            startInfo.phoneNumber,
            startInfo.begin,
            startInfo.duration
        )
    }

    override fun contains(number: PhoneNumber): Boolean {
        return database.startInfoQueries.containsPhoneNumber(number).executeAsOne() >= 1L
    }

    override fun getByPhoneNumber(phoneNumber: PhoneNumber): StartInfo? {
        return database.startInfoQueries.getByPhoneNumber(phoneNumber, mapper).executeAsOneOrNull()
    }
}