package ru.spbstu.application

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.spbstu.application.auth.repository.SubscriptionRepository
import ru.spbstu.application.auth.repository.SubscriptionRepositoryImpl
import ru.spbstu.application.auth.repository.UserRepository
import ru.spbstu.application.auth.repository.UserRepositoryImpl
import ru.spbstu.application.auth.usecases.IsSubscribedUseCase
import ru.spbstu.application.auth.usecases.RegisterUserUseCase
import ru.spbstu.application.data.DatabaseTransaction
import ru.spbstu.application.data.DatabaseTransactionImpl
import ru.spbstu.application.data.createAppDatabase
import ru.spbstu.application.steps.repository.CompletedStepRepository
import ru.spbstu.application.steps.repository.CompletedStepRepositoryImpl
import ru.spbstu.application.steps.repository.StepDurationRepository
import ru.spbstu.application.steps.repository.StepDurationRepositoryImpl
import ru.spbstu.application.telegram.TelegramBot
import ru.spbstu.application.trendyfriendy.trendyFriendyModule

val unistartModule = module(createdAtStart = true) {
    includes(trendyFriendyModule)
    val appConfig = readAppConfig()
    single { appConfig }
    single { appConfig.telegramToken }
    single { createAppDatabase(appConfig.jdbcString) }
    singleOf(::DatabaseTransactionImpl) bind DatabaseTransaction::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::StepDurationRepositoryImpl) bind StepDurationRepository::class
    singleOf(::SubscriptionRepositoryImpl) bind SubscriptionRepository::class
    singleOf(::CompletedStepRepositoryImpl) bind CompletedStepRepository::class
    singleOf(::RegisterUserUseCase)
    singleOf(::IsSubscribedUseCase)
    singleOf(::TelegramBot)
}
