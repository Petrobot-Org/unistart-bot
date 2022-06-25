package ru.spbstu.application

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.spbstu.application.auth.repository.FakeUserRepository
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.telegram.TelegramBot

val unistartModule = module(createdAtStart = true) {
    val appConfig = readAppConfig()
    single { appConfig.telegramToken }
    singleOf(::FakeUserRepository) bind UserRepository::class
    singleOf(::TelegramBot)
}
