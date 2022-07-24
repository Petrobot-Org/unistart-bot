package ru.spbstu.application.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.spbstu.application.serializers.LocalTimeSerializer
import ru.spbstu.application.serializers.PeriodSerializer
import java.time.LocalTime
import java.time.Period

@Serializable
class NotificationsConfig(
    @SerialName("next_step") val nextStep: NextStep
) {
    @Serializable
    class NextStep(
        @Serializable(with = PeriodSerializer::class)
        @SerialName("after_days") val after: Period,
        @Serializable(with = LocalTimeSerializer::class)
        @SerialName("at") val at: LocalTime
    )
}
