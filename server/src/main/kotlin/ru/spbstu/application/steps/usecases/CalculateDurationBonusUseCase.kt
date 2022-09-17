package ru.spbstu.application.steps.usecases

import ru.spbstu.application.AppConfig
import ru.spbstu.application.extensions.times
import ru.spbstu.application.steps.entities.Step
import java.time.Duration
import java.time.Instant

class CalculateDurationBonusUseCase(
    private val config: AppConfig,
    private val getStepDuration: GetStepDurationUseCase
) {
    operator fun invoke(step: Step, startedAt: Instant, endedAt: Instant): Int {
        val baseStepDuration = getStepDuration(step).duration
        return config.durationToBonus
            .filter { Duration.between(startedAt, endedAt) < baseStepDuration * it.durationFactor }
            .maxOfOrNull { it.bonus } ?: 0
    }
}
