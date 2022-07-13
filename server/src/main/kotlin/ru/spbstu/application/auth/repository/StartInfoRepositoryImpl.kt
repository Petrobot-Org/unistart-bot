package ru.spbstu.application.auth.repository

import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.StartInfo
import ru.spbstu.application.data.source.AppDatabase
//import java.time.Instant

class StartInfoRepositoryImpl(private val database: AppDatabase) : StartInfoRepository  {
//    private val mapper = { id: StartInfo.Id, number: PhoneNumber, begin: Instant, end: Instant ->
//        StartInfo(id, number, begin, end)
//    }
    override fun add(startInfo: StartInfo) {
        database.startInfoQueries.add(
            startInfo.id,
            startInfo.number,
            startInfo.begin,
            startInfo.end)
    }

    override fun contains(number: PhoneNumber): Boolean {
        return database.startInfoQueries.containsPhoneNumber(number).executeAsOne() >= 1L
    }
}