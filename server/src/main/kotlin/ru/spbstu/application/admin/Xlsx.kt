package ru.spbstu.application.admin

import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.spbstu.application.auth.entities.PhoneNumber
import ru.spbstu.application.steps.entities.Step
import ru.spbstu.application.steps.entities.UserWithCompletedSteps
import ru.spbstu.application.telegram.Strings
import trendyfriendy.TrendCard
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.temporal.ChronoUnit

object Xlsx {
    sealed interface Result<T> {
        class OK<T>(val value: T) : Result<T>
        class BadFormat<T>(val errorRows: List<Int>) : Result<T>
    }

    fun parsePhoneNumbers(inputStream: InputStream): Result<List<PhoneNumber>> {
        val phoneNumbers = XSSFWorkbook(inputStream).use { workbook ->
            workbook.getSheetAt(0).map { row ->
                try {
                    row.getCell(0).stringCellValue
                } catch (ignore: IllegalStateException) {
                    String()
                }
            }
        }.dropLastWhile { it.isEmpty() }.map { PhoneNumber.valueOf(it.removePrefix("+")) }
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

    fun parseTrends(inputStream: InputStream, urlPrefix: String): Result<Map<String, List<TrendCard>>> {
        val cards = XSSFWorkbook(inputStream).use { workbook ->
            workbook.getSheetAt(0).map { row ->
                try {
                    val trendSetName = row.getCell(0).stringCellValue
                    val card = TrendCard(
                        name = row.getCell(1).stringCellValue,
                        description = row.getCell(2).stringCellValue,
                        url = urlPrefix + row.getCell(3).stringCellValue
                    )
                    trendSetName to card
                } catch (e: Exception) {
                    null
                }
            }
        }
        if (cards.contains(null)) {
            return Result.BadFormat(cards.mapIndexedNotNull { index, value -> if (value == null) index else null })
        }
        val sets = cards
            .filterNotNull()
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
        return Result.OK(sets)
    }

    fun createStatisticsSpreadsheet(info: List<UserWithCompletedSteps>): ByteArray {
        val workbook = XSSFWorkbook()
        workbook.createSheet().apply {
            val style = workbook.createCellStyle().apply {
                setFont(workbook.createFont())
                alignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.BOTTOM
                wrapText = false
            }
            createRow(0).apply {
                listOf(
                    Strings.StatisticsSpreadsheetPhoneNumber,
                    Strings.StatisticsSpreadsheetDuration
                ).forEachIndexed { index, s ->
                    createCell(index).apply {
                        setCellValue(s)
                        cellStyle = style
                    }
                }
                createCell(5).apply {
                    setCellValue(Strings.StatisticsSpreadsheetExtraPoints)
                    cellStyle = style
                }
                createCell(6).apply {
                    setCellValue(Strings.StatisticsSpreadsheetOccupation)
                    cellStyle = style
                }
            }
            createRow(1).apply {
                (1..4).forEach { index ->
                    createCell(index).apply {
                        setCellValue(Strings.StatisticsSpreadsheetStep(Step(index.toLong())))
                        cellStyle = style
                    }
                }
            }
            addMergedRegion(CellRangeAddress(0, 1, 0, 0))
            addMergedRegion(CellRangeAddress(0, 0, 1, 4))
            addMergedRegion(CellRangeAddress(0, 1, 5, 5))
            addMergedRegion(CellRangeAddress(0, 1, 6, 6))
            info.forEachIndexed { index, userWithCompletedSteps ->
                createRow(index + 2).apply {
                    createCell(0).setCellValue("+" + userWithCompletedSteps.user.phoneNumber.value)
                    userWithCompletedSteps.completedSteps.sortedBy { t -> t.step.value }
                        .zipWithNext { st1, st2 ->
                            ChronoUnit.DAYS.between(st1.endTime, st2.endTime).toDouble()
                        }.forEachIndexed { index, s ->
                            createCell(index + 1).setCellValue(s)
                        }
                    createCell(5).setCellValue(userWithCompletedSteps.user.amountOfCoins.toDouble())
                    createCell(6).setCellValue(Strings.Occupations[userWithCompletedSteps.user.occupation])
                }
            }
            autoSizeColumn(0, true)
            autoSizeColumn(1, true)
            autoSizeColumn(2, true)
            autoSizeColumn(3, true)
            autoSizeColumn(4, true)
            autoSizeColumn(5, true)
            autoSizeColumn(6, true)
        }
        return ByteArrayOutputStream().apply {
            workbook.write(this)
            workbook.close()
        }.toByteArray()
    }
}
