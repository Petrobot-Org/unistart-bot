package ru.spbstu.application

import com.ithersta.tgbotapi.fsm.repository.StateRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.spbstu.application.admin.TrendsZip
import ru.spbstu.application.admin.usecases.AddAdminsUseCase
import ru.spbstu.application.admin.usecases.AddPhoneNumbersUseCase
import ru.spbstu.application.auth.repository.*
import ru.spbstu.application.auth.usecases.*
import ru.spbstu.application.data.*
import ru.spbstu.application.notifications.ConfigureNotifiers
import ru.spbstu.application.notifications.NextStepNotifier
import ru.spbstu.application.steps.repository.*
import ru.spbstu.application.steps.usecases.CalculateDurationBonusUseCase
import ru.spbstu.application.steps.usecases.CheckAndUpdateBonusAccountingUseCase
import ru.spbstu.application.steps.usecases.GetStepDurationUseCase
import ru.spbstu.application.telegram.TelegramBot
import ru.spbstu.application.telegram.createStateMachine
import ru.spbstu.application.telegram.repository.UserDialogStateRepository
import ru.spbstu.application.trendyfriendy.trendyFriendyModule
import java.time.ZoneId

val unistartModule = module(createdAtStart = true) {
    includes(trendyFriendyModule)
    val appConfig = readAppConfig()
    single { readDatabaseCredentials() }
    single { createDataSource(get()) }
    single { createAppDatabase(get()) }
    single { appConfig }
    single { ZoneId.of(appConfig.timezone) }
    single { createStateMachine(get(), get()) }
    singleOf(::DatabaseTransactionImpl) bind DatabaseTransaction::class
    singleOf(::DatabaseTransactionWithResultImpl) bind DatabaseTransactionWithResult::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::StepDurationRepositoryImpl) bind StepDurationRepository::class
    singleOf(::SubscriptionRepositoryImpl) bind SubscriptionRepository::class
    singleOf(::CompletedStepRepositoryImpl) bind CompletedStepRepository::class
    singleOf(::StartInfoRepositoryImpl) bind StartInfoRepository::class
    singleOf(::AdminRepositoryImpl) bind AdminRepository::class
    singleOf(::UserDialogStateRepository) bind StateRepository::class
    singleOf(::BonusAccountingRepositoryImpl) bind BonusAccountingRepository::class
    singleOf(::AddAdminsUseCase)
    singleOf(::IsRootAdminUseCase)
    singleOf(::IsNonRootAdminUseCase)
    singleOf(::IsAdminUseCase)
    singleOf(::GetUserUseCase)
    singleOf(::GetStepDurationUseCase)
    singleOf(::AddPhoneNumbersUseCase)
    singleOf(::RegisterUserUseCase)
    singleOf(::RegisterAdminUserUseCase)
    singleOf(::IsSubscribedUseCase)
    singleOf(::CalculateDurationBonusUseCase)
    singleOf(::CheckAndUpdateBonusAccountingUseCase)
    singleOf(::TrendsZip)
    singleOf(::NextStepNotifier)
    singleOf(::ConfigureNotifiers)
    singleOf(::TelegramBot)
}
