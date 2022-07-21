package ru.spbstu.application.admin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.steps.entities.CompletedStep
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.UserWithCompletedSteps
import trendyfriendy.TrendCard
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

internal class XlsxTest {

    @Test
    fun parsePhoneNumbers() {
        File("src/test/testPhoneBook.xlsx").inputStream().use { inputStream ->
            when (val result = Xlsx.parsePhoneNumbers(inputStream)) {
                is Xlsx.Result.OK -> {
                    val expected =
                        listOf("000", "111", "222", "333", "444", "555", "666", "777", "888", "999", "111")
                            .map { PhoneNumber.valueOf(it) }
                    assertEquals(expected, result.value)
                }
                is Xlsx.Result.BadFormat -> fail("Bad format in rows ${result.errorRows}")
                is Xlsx.Result.InvalidFile -> fail("Invalid file")
            }
        }
    }

    @Test
    fun parseTrends() {
        val expected = mapOf(
            "Gartner" to listOf(
                TrendCard("Тренд 1", "Описание", "pic1.jpeg"),
                TrendCard("Тренд 2", "Описание", "pic1.jpeg"),
                TrendCard("Тренд 3", "Описание", "pic1.jpeg"),
            ),
            "Skolkovo" to listOf(
                TrendCard("Тренд 4", "Описание", "pic1.jpeg"),
                TrendCard("Тренд 5", "Описание", "pic1.jpeg"),
                TrendCard("Тренд 6", "Описание", "pic1.jpeg"),
                TrendCard("Тренд 7", "Описание", "pic1.jpeg"),
            )
        )
        File("src/test/trends.xlsx").inputStream().use { inputStream ->
            when (val result = Xlsx.parseTrends(inputStream)) {
                is Xlsx.Result.OK -> assertEquals(expected, result.value)
                is Xlsx.Result.BadFormat -> fail("Bad format in rows ${result.errorRows}")
                is Xlsx.Result.InvalidFile -> fail("Invalid file")
            }
        }
    }

    @Test
    fun createStatisticFile() {
        val list = listOf(
            UserWithCompletedSteps(
                User(
                    User.Id(12), PhoneNumber.valueOf("19")!!,
                    Avatar.DigitalAgile, Occupation.BachelorYear1, 20, 20
                ),
                listOf(
                    CompletedStep(User.Id(12), Step(0), Instant.now()),
                    CompletedStep(User.Id(12), Step(2), Instant.now().plusSeconds(86400 * 2)),
                    CompletedStep(User.Id(12), Step(1), Instant.now().plusSeconds(86400)),
                    CompletedStep(User.Id(12), Step(3), Instant.now().plusSeconds(86400 * 3 - 1000)),
                    CompletedStep(User.Id(12), Step(4), Instant.now().plusSeconds(86400 * 4 + 1000))
                )
            ),
            UserWithCompletedSteps(
                User(
                    User.Id(1), PhoneNumber.valueOf("20")!!,
                    Avatar.DigitalAgile, Occupation.BachelorYear1, 20, 20
                ),
                listOf(
                    CompletedStep(User.Id(1), Step(0), Instant.now()),
                    CompletedStep(User.Id(1), Step(2), Instant.now().plusSeconds(86400 * 2)),
                    CompletedStep(User.Id(1), Step(1), Instant.now().plusSeconds(86400))
                )
            )
        )
        val res = Xlsx.createStatisticsSpreadsheet(list)
        FileOutputStream("src/test/stats.xlsx").write(res)
    }
}
