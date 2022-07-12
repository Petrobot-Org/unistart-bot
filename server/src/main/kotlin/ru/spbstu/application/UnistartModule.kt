package ru.spbstu.application

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.spbstu.application.admin.usecases.IsAdminUseCase
import ru.spbstu.application.auth.repository.SubscriptionRepository
import ru.spbstu.application.auth.repository.SubscriptionRepositoryImpl
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.auth.repository.UserRepositoryImpl
import ru.spbstu.application.auth.usecases.IsSubscribedUseCase
import ru.spbstu.application.data.createAppDatabase
import ru.spbstu.application.steps.repository.StepDurationRepository
import ru.spbstu.application.steps.repository.StepDurationRepositoryImpl
import ru.spbstu.application.steps.usecases.GetStepDurationUseCase
import ru.spbstu.application.telegram.TelegramBot
import ru.spbstu.application.trendyfriendy.trendyFriendyModule
import java.time.ZoneId

val unistartModule = module(createdAtStart = true) {
    includes(trendyFriendyModule)
    val appConfig = readAppConfig()
    val secrets = readSecrets()
    single { appConfig }
    single { secrets.telegramToken }
    single { createAppDatabase(appConfig.jdbcString) }
    single { ZoneId.of(appConfig.timezone) }
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::StepDurationRepositoryImpl) bind StepDurationRepository::class
    singleOf(::SubscriptionRepositoryImpl) bind SubscriptionRepository::class
    singleOf(::IsAdminUseCase)
    singleOf(::GetStepDurationUseCase)
    singleOf(::IsSubscribedUseCase)
    singleOf(::TelegramBot)
}
