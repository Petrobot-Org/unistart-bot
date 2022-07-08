package ru.spbstu.application.data

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.data.adapters.*
import ru.spbstu.application.data.source.AppDatabase
import ru.spbstu.application.data.source.Step
import ru.spbstu.application.data.source.Subscription
import ru.spbstu.application.data.source.User
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
        StepAdapter = Step.Adapter(
            idAdapter = StepIdAdapter,
            startAdapter = StartAdapter,
            durationAdapter = DurationAdapter
        ),
        SubscriptionAdapter = Subscription.Adapter(
            idAdapter = SubscriptionIdAdapter,
            startAdapter = StartAdapter,
            durationAdapter = DurationAdapter,
            user_idAdapter = UserIdAdapter

        ),
        UserAdapter = User.Adapter(
            idAdapter = UserIdAdapter,
            phoneNumberAdapter = UserPhoneNumberAdapter,
            avatarAdapter = EnumColumnAdapter<Avatar>(),
            occupationAdapter = EnumColumnAdapter<Occupation>()
        )
    )
}
