package ru.spbstu.application.data

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import ru.spbstu.application.data.source.AppDatabase
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
    //недописано
    TODO()
}
