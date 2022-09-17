package ru.spbstu.application.admin

import org.apache.poi.ss.usermodel.*
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
        class InvalidFile<T> : Result<T>
    }

    fun parsePhoneNumbers(inputStream: InputStream): Result<List<PhoneNumber>> =
        runCatching {
            val phoneNumbers = XSSFWorkbook(inputStream).use { parsePhones(it) }
            if (phoneNumbers.second.isEmpty()) {
                Result.OK(phoneNumbers.first.requireNoNulls())
            } else {
                Result.BadFormat(phoneNumbers.second)
            }
        }.getOrElse {
            Result.InvalidFile()
        }

    private fun parsePhones(
        workbook: Workbook
    ): Pair<List<PhoneNumber?>, List<Int>> {
        return workbook.getSheetAt(0)
            .map { it.getCellText(0) }
            .dropLastWhile { it.isNullOrBlank() }
            .map {
                it?.let { text -> PhoneNumber.valueOf(text.removePrefix("+")) }
            }
            .let {
                it to it.mapIndexedNotNull { index, e -> (index + 1).takeIf { e == null } }
            }
    }

    private fun Row.getCellText(number: Int) = runCatching {
        val cell = getCell(number)
        when (cell.cellType) {
            CellType.NUMERIC -> cell.numericCellValue.toLong().toString()
            CellType.STRING -> cell.stringCellValue
            CellType.BLANK -> ""
            else -> null
        }
    }.getOrNull()

    fun parseTrends(inputStream: InputStream): Result<Map<String, List<TrendCard>>> {
        try {
            val cards = XSSFWorkbook(inputStream).use { workbook ->
                workbook.getSheetAt(0).map { row ->
                    try {
                        val trendSetName = row.getCell(0).stringCellValue
                        val card = TrendCard(
                            name = row.getCell(1).stringCellValue,
                            description = row.getCell(2).stringCellValue,
                            filename = row.getCell(3).stringCellValue
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
        } catch (e: Exception) {
            return Result.InvalidFile()
        }
    }

    fun createStatisticsSpreadsheet(info: List<UserWithCompletedSteps>): ByteArray {
        require(Step.LastValue == 4)
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
                        setCellValue(Strings.StatisticsSpreadsheetStep(Step(index)))
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
