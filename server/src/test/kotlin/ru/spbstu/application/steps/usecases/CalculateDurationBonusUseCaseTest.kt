package ru.spbstu.application.steps.usecases

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

import ru.spbstu.application.AppConfig
import ru.spbstu.application.DurationBonus
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.StepDuration
import java.time.Duration
import java.time.Instant

internal class CalculateDurationBonusUseCaseTest {

    @Test
    operator fun invoke() {
        val startedAt = Instant.now()
        val step = Step(1L)
        val config = AppConfig(
            durationToBonus = listOf(
                DurationBonus(0.5, 3L),
                DurationBonus(1.0, 2L),
                DurationBonus(2.0, 1L),
            )
        )
        val getStepDurationUseCase = mockk<GetStepDurationUseCase>()
        every { getStepDurationUseCase.invoke(step) } returns StepDuration(step, Duration.ofDays(20))
        val calculateDurationBonus = CalculateDurationBonusUseCase(config, getStepDurationUseCase)
        assert(calculateDurationBonus(step, startedAt, startedAt.plus(Duration.ofDays(9))) == 3L)
        assert(calculateDurationBonus(step, startedAt, startedAt.plus(Duration.ofDays(11))) == 2L)
        assert(calculateDurationBonus(step, startedAt, startedAt.plus(Duration.ofDays(21))) == 1L)
        assert(calculateDurationBonus(step, startedAt, startedAt.plus(Duration.ofDays(41))) == 0L)
    }
}
