package ru.spbstu.application.steps.usecases

import ru.spbstu.application.AppConfig
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.StepDuration
import ru.spbstu.application.steps.repository.StepDurationRepository
import java.time.Duration

class GetStepDurationUseCase(
    private val appConfig: AppConfig,
    private val stepDurationRepository: StepDurationRepository
) {
    operator fun invoke(step: Step): StepDuration {
        return stepDurationRepository.get(step) ?: run {
            val seconds = appConfig.defaultStepDurationsSeconds.getValue(step)
            StepDuration(step, Duration.ofSeconds(seconds))
        }
    }
}
