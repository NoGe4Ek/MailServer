package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.ExcelHandler
import com.poly.intelligentmessaging.mailserver.domain.dto.ExcelDTO
import com.poly.intelligentmessaging.mailserver.repositories.FilterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class ExcelService {

    @Autowired
    private val excelHandler: ExcelHandler? = null

    @Autowired
    private val filterRepository: FilterRepository? = null

    fun generateExcel(excelDTO: ExcelDTO): Pair<ByteArray, String> {
        val filter = filterRepository!!.findById(UUID.fromString(excelDTO.idFilter)).get()
        val students = filter.students!!
        val name = "${filter.created!!.toLocalDate()}-${filter.name}.xlsx"
        val excelFile = excelHandler!!.generateExcel(students, name)
        val bytes = excelFile.readBytes()
        excelFile.delete()
        return bytes to name
    }
}
