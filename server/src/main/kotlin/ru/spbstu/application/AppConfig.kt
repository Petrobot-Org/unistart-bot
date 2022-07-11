package ru.spbstu.application

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.telegram.Strings
import ru.spbstu.application.telegram.TelegramToken
import java.io.FileInputStream
import java.util.*

class Secrets(
    val telegramToken: TelegramToken
)

@Serializable
class AppConfig(
    @SerialName("jdbc") val jdbcString: String,
    @SerialName("public_hostname") val publicHostname: String,
    @SerialName("root_admin_user_ids") val rootAdminUserIds: Collection<User.Id>
)

fun readSecrets(): Secrets {
    return Secrets(
        telegramToken = TelegramToken(System.getenv("TELEGRAM_TOKEN"))
    )
}

fun readAppConfig(): AppConfig {
    return readConfig("application.yaml")
}

inline fun <reified T> readConfig(path: String): T {
    return try {
        readCustomConfig(path)
    } catch (e: Exception) {
        readDefaultConfig(path)
    }
}

inline fun <reified T> readDefaultConfig(path: String): T {
    val inputStream = AppConfig::class.java.getResourceAsStream("/$path")!!
    return Yaml.default.decodeFromStream(inputStream)
}

inline fun <reified T> readCustomConfig(path: String): T {
    val inputStream = FileInputStream(path)
    return Yaml.default.decodeFromStream(inputStream)
}
