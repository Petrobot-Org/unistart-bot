package ru.spbstu.application.data

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import mu.KotlinLogging
import ru.spbstu.application.data.adapters.*
import ru.spbstu.application.data.source.*
import java.util.*
import javax.sql.DataSource

private val logger = KotlinLogging.logger { }

fun createAppDatabase(dataSource: DataSource): AppDatabase {
    val driver = dataSource.asJdbcDriver()
    return AppDatabase(
        driver = driver,
        StepDurationAdapter = StepDuration.Adapter(
            stepAdapter = StepAdapter,
            durationAdapter = DurationAdapter
        ),
        SubscriptionAdapter = Subscription.Adapter(
            idAdapter = SubscriptionIdAdapter,
            startAdapter = InstantAdapter,
            durationAdapter = DurationAdapter,
            userIdAdapter = UserIdAdapter
        ),
        StartInfoAdapter = StartInfo.Adapter(
            idAdapter = StartInfoIdAdapter,
            phoneNumberAdapter = UserPhoneNumberAdapter,
            beginAdapter = InstantAdapter,
            durationAdapter = DurationAdapter
        ),
        UsersAdapter = Users.Adapter(
            idAdapter = UserIdAdapter,
            phoneNumberAdapter = UserPhoneNumberAdapter,
            avatarAdapter = EnumColumnAdapter(),
            occupationAdapter = EnumColumnAdapter()
        ),
        CompletedStepAdapter = CompletedStep.Adapter(
            userIdAdapter = UserIdAdapter,
            stepAdapter = StepAdapter,
            endTimeAdapter = InstantAdapter
        ),
        AdminAdapter = Admin.Adapter(
            idAdapter = UserIdAdapter
        ),
        BonusAccountingAdapter = BonusAccounting.Adapter(
            userIdAdapter = UserIdAdapter,
            bonusTypeAdapter = EnumColumnAdapter()
        ),
        UserDialogStateAdapter = UserDialogState.Adapter(
            userIdAdapter = UserIdAdapter,
            stateAdapter = DialogStateAdapter
        )
    ).also {
        runCatching {
            AppDatabase.Schema.create(driver)
            AppDatabase.Schema.migrate(driver, 1, 2)
        }.onFailure {
            logger.info("Database schema wasn't created")
        }
    }
}
