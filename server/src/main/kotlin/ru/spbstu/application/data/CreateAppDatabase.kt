package ru.spbstu.application.data

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import mu.KotlinLogging
import ru.spbstu.application.data.adapters.*
import ru.spbstu.application.data.source.*
import java.sql.SQLException
import java.util.*

private val logger = KotlinLogging.logger { }

fun createAppDatabase(jdbcString: String): AppDatabase {
    val driver = JdbcSqliteDriver(
        jdbcString,
        Properties(1).apply { put("foreign_keys", "true") }
    ).also {
        try {
            AppDatabase.Schema.create(it)
        } catch (e: SQLException) {
            logger.info { "Couldn't create schema. Already created?" }
        }
    }
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
            user_idAdapter = UserIdAdapter
        ),
        StartInfoAdapter = StartInfo.Adapter(
            idAdapter = StartInfoIdAdapter,
            phoneNumberAdapter = UserPhoneNumberAdapter,
            beginAdapter = InstantAdapter,
            durationAdapter = DurationAdapter
        ),
        UserAdapter = User.Adapter(
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
        )
    )
}
