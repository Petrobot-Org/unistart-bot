package ru.spbstu.application.admin

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.spbstu.application.auth.entities.PhoneNumber
import java.io.InputStream

object Xlsx {
    sealed interface Result<T> {
        class OK<T>(val value: T) : Result<T>
        class BadFormat<T>(val errorRows: List<Int>) : Result<T>
    }

    fun parsePhoneNumbers(inputStream: InputStream): Result<List<PhoneNumber>> {
        val phoneNumbers = XSSFWorkbook(inputStream).getSheetAt(0).map { row ->
            try {
                row.getCell(0).stringCellValue
            } catch (ignore: IllegalStateException) {
                String()
            }
        }.dropLastWhile { it.isEmpty() }.map { PhoneNumber.valueOf(it) }
        return if (!phoneNumbers.contains(null)) {
            Result.OK(phoneNumbers.filterNotNull())
        } else {
            Result.BadFormat(phoneNumbers.mapIndexedNotNull { index, phoneNumber ->
                if (phoneNumber == null) {
                    index
                } else {
                    null
                }
            })
        }
    }
}
