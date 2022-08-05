package ru.spbstu.application.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.spbstu.application.serializers.DurationSerializer
import java.time.Duration

@Serializable
class NotificationsConfig(
    @SerialName("next_step") val nextStep: NextStep = NextStep()
) {
    @Serializable
    class NextStep(
        @Serializable(with = DurationSerializer::class)
        @SerialName("before_seconds")
        val before: Duration = Duration.ofDays(1)
    )
}
