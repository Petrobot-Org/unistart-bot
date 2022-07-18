package ru.spbstu.application.data

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import ru.spbstu.application.data.adapters.*
import ru.spbstu.application.data.source.*
import java.sql.SQLException
import java.util.*

fun createAppDatabase(jdbcString: String): AppDatabase {
    val driver = JdbcSqliteDriver(
        jdbcString,
        Properties(1).apply { put("foreign_keys", "true") }
    ).also {
        try {
            AppDatabase.Schema.create(it)
        } catch (e: SQLException) {
            e.printStackTrace()
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
        BonusAccountingAdapter = BonusAccounting.Adapter(
            idAdapter = BonusAccountingIdAdapter,
            userIdAdapter = UserIdAdapter,
            bonusTypeAdapter =  EnumColumnAdapter()
        )
    )
}
