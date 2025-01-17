package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.domain.StudentExcel
import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream


@Component
class ExcelHandler {

    fun parseExcel(file: MultipartFile): Pair<MutableSet<StudentExcel>, MutableMap<String, MutableSet<String>>> {
        val tempFile = File(file.originalFilename!!)
        val os = FileOutputStream(tempFile)
        os.write(file.bytes)
        val workBook = XSSFWorkbook(tempFile)
        val sheet = workBook.getSheetAt(0)

        val students = mutableSetOf<StudentExcel>()
        val groupAttributes = getGroupsAttributeFromFirstRow(sheet.first())
        for (row in sheet) {
            if (row == sheet.first() || row.getCell(5) == null) continue
            val email = row.getCell(5).stringCellValue
            val studentExcel = StudentExcel(
                lastName = if (row.getCell(0) != null) row.getCell(0).stringCellValue else "",
                firstName = if (row.getCell(1) != null) row.getCell(1).stringCellValue else "",
                patronymic = if (row.getCell(2) != null) row.getCell(2).stringCellValue else "",
                email = email
            )
            val attributes = mutableMapOf<String, String>()
            attributes[sheet.getRow(0).getCell(3).stringCellValue] = row.getCell(3).stringCellValue
            attributes[sheet.getRow(0).getCell(4).stringCellValue] = row.getCell(4).stringCellValue
            for (cellIndex in 6 until row.lastCellNum) {
                if (row.getCell(cellIndex) == null) continue
                val key = sheet.getRow(0).getCell(cellIndex).stringCellValue
                val value: String =
                    if (row.getCell(cellIndex).cellType == CellType.STRING) parseValue(row.getCell(cellIndex).stringCellValue)
                    else row.getCell(cellIndex).numericCellValue.toInt().toString()
                attributes[key] = value
                groupAttributes[key]!!.add(value)
            }
            studentExcel.attributes = attributes
            students.add(studentExcel)
        }
        tempFile.delete()
        return students to groupAttributes
    }

    fun generateExcel(students: Set<StudentModel>, name: String): File {
        val listStudents = students.toList()

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(name)

        val headerCellStyle = workbook.createCellStyle()
        headerCellStyle.alignment = HorizontalAlignment.CENTER
        headerCellStyle.verticalAlignment = VerticalAlignment.CENTER
        sheet.setColumnWidth(0, 10976)
        sheet.setColumnWidth(1, 10976)


        val firstRow = sheet.createRow(0)

        firstRow.createCell(0).setCellValue("ФИО")
        firstRow.createCell(1).setCellValue("Email")

        firstRow.getCell(0).cellStyle = headerCellStyle
        firstRow.getCell(1).cellStyle = headerCellStyle

        for (i in listStudents.indices) {
            val person = listStudents[i].person!!
            val fullName = "${person.lastName} ${person.firstName} ${person.patronymic}"
            val row = sheet.createRow(i + 1)
            row.createCell(0).setCellValue(fullName)
            row.createCell(1).setCellValue(person.email!!)
        }
        val fileOut = FileOutputStream(name)
        workbook.write(fileOut)
        fileOut.close()
        workbook.close()
        return File(name)
    }

    private fun getGroupsAttributeFromFirstRow(row: Row): MutableMap<String, MutableSet<String>> {
        val groupsAttribute = mutableMapOf<String, MutableSet<String>>()
        groupsAttribute[row.getCell(3).stringCellValue] = mutableSetOf()
        groupsAttribute[row.getCell(4).stringCellValue] = mutableSetOf()
        for (cellIndex in 6 until row.lastCellNum) {
            groupsAttribute[row.getCell(cellIndex).stringCellValue] = mutableSetOf()
        }
        return groupsAttribute
    }

    private fun parseValue(value: String): String {
        return if (value.contains("-")) {
            val parts = value.split("-")
            parts[parts.lastIndex].trim()
        } else value
    }
}
