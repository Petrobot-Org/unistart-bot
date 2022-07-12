package ru.spbstu.application.admin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import ru.spbstu.application.auth.entities.PhoneNumber
import java.io.File

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
                is Xlsx.Result.BadFormat -> {
                    fail("Bad format in rows ${result.errorRows}")
                }
            }
        }
    }
}
