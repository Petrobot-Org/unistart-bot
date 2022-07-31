package ru.spbstu.application.scamper

import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream

object Xlsx {
    fun createScamperSpreadsheet(answers: List<ScamperAnswer>, questionnaire: Questionnaire): ByteArray {
        val preparedAnswers = answers
            .groupBy { it.letterIndex }
            .toSortedMap()
            .mapKeys { questionnaire.letters[it.key] }
            .mapValues { letterGroup ->
                letterGroup.value
                    .groupBy { it.questionIndex }
                    .mapValues { questionGroup -> questionGroup.value.map { it.text } }
                    .mapKeys { letterGroup.key.questions[it.key] }
            }
        val workbook = XSSFWorkbook()
        var rowNumber = 0
        workbook.createSheet().apply {
            preparedAnswers.forEach { (letter, letterGroup) ->
                createRow(rowNumber++).createCell(0).apply {
                    cellStyle = workbook.createCellStyle().apply {
                        setFont(workbook.createFont().apply {
                            bold = true
                        })
                    }
                    setCellValue(letter.character.toString())
                }
                letterGroup.forEach { (question, questionGroup) ->
                    if (questionGroup.size > 1) {
                        addMergedRegion(CellRangeAddress(rowNumber, rowNumber + questionGroup.lastIndex, 0, 0))
                    }
                    questionGroup.forEachIndexed { index, answer ->
                        createRow(rowNumber++).apply {
                            if (index == 0) createCell(0).setCellValue(question)
                            createCell(1).setCellValue(answer)
                        }
                    }
                }
            }
            autoSizeColumn(0, true)
            autoSizeColumn(1)
        }
        return ByteArrayOutputStream().apply {
            workbook.write(this)
            workbook.close()
        }.toByteArray()
    }
}
