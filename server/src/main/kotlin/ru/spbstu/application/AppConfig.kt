package ru.spbstu.application

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.notifications.NotificationsConfig
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.telegram.TelegramToken
import java.io.FileInputStream
import kotlin.math.log

private val logger = KotlinLogging.logger {  }

class Secrets(
    val telegramToken: TelegramToken
)

@Serializable
class AppConfig(
    val timezone: String,
    @SerialName("jdbc") val jdbcString: String,
    @SerialName("public_hostname") val publicHostname: String,
    @SerialName("root_admin_user_ids") val rootAdminUserIds: Collection<User.Id>,
    @SerialName("default_step_durations_seconds") val defaultStepDurationsSeconds: Map<Step, Long>,
    @SerialName("notifications") val notifications: NotificationsConfig
)

fun readSecrets(): Secrets {
    return Secrets(
        telegramToken = TelegramToken(System.getenv("TELEGRAM_TOKEN"))
    )
}

fun readAppConfig(): AppConfig {
    return readConfig("application.yaml")
}

private inline fun <reified T> readConfig(path: String): T {
    return try {
        readCustomConfig(path)
    } catch (e: Exception) {
        logger.warn { "$path is malformed. Loading default config." }
        readDefaultConfig(path)
    }
}

private inline fun <reified T> readDefaultConfig(path: String): T {
    return AppConfig::class.java.getResourceAsStream("/$path")!!.use { inputStream ->
        Yaml.default.decodeFromStream(inputStream)
    }
}

private inline fun <reified T> readCustomConfig(path: String): T {
    return FileInputStream(path).use { inputStream ->
        Yaml.default.decodeFromStream(inputStream)
    }
}
