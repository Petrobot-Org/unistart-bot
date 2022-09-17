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

private val logger = KotlinLogging.logger { }

class Secrets(
    val telegramToken: TelegramToken
)

@Serializable
class DurationBonus(
    @SerialName("duration_factor") val durationFactor: Double,
    @SerialName("bonus") val bonus: Int
)

@Serializable
class AppConfig(
    val timezone: String = "Europe/Moscow",
    @SerialName("public_hostname") val publicHostname: String = "https://127.0.0.1",
    @SerialName("root_admin_user_ids") val rootAdminUserIds: Collection<User.Id> = emptyList(),
    @SerialName("default_step_durations_seconds") val defaultStepDurationsSeconds: Map<Step, Long> = mapOf(
        Step(1) to 604_800,
        Step(2) to 604_800,
        Step(3) to 604_800,
        Step(4) to 604_800
    ),
    @SerialName("notifications") val notifications: NotificationsConfig = NotificationsConfig(),
    @SerialName("duration_to_bonus") val durationToBonus: Collection<DurationBonus> = listOf(
        DurationBonus(0.5, 8),
        DurationBonus(1.0, 5),
        DurationBonus(2.0, 3)
    )
)

fun readSecrets(): Secrets {
    return Secrets(
        telegramToken = TelegramToken(System.getenv("TELEGRAM_TOKEN"))
    )
}

fun readAppConfig(): AppConfig {
    return readConfig("application.yaml") { AppConfig() }
}

private inline fun <reified T> readConfig(path: String, default: () -> T): T {
    return try {
        readCustomConfig(path)
    } catch (e: Exception) {
        logger.warn { "$path is malformed. Loading default config." }
        default()
    }
}

private inline fun <reified T> readCustomConfig(path: String): T {
    return FileInputStream(path).use { inputStream ->
        Yaml.default.decodeFromStream(inputStream)
    }
}
