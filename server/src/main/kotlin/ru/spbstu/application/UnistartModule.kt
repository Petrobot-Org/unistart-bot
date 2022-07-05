package ru.spbstu.application

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.spbstu.application.auth.repository.UserRepositoryImpl
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.data.createAppDatabase
import ru.spbstu.application.telegram.TelegramBot
import ru.spbstu.application.trendyfriendy.TrendyFriendyService
import ru.spbstu.application.trendyfriendy.trendyFriendyModule

val unistartModule = module(createdAtStart = true) {
    includes(trendyFriendyModule)
    val appConfig = readAppConfig()
    single { appConfig }
    single { appConfig.telegramToken }
    single { createAppDatabase(appConfig.jdbcString) }
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::TelegramBot)
}
