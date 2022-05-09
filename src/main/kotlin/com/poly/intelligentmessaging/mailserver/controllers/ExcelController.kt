package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.ExcelDTO
import com.poly.intelligentmessaging.mailserver.services.ExcelService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/excel")
class ExcelController {

    @Autowired
    private val excelService: ExcelService? = null

    @PostMapping("/download")
    fun downloadExcel(@RequestBody excelDTO: ExcelDTO): ResponseEntity<ByteArray> {
        val (resource, name) = excelService!!.generateExcel(excelDTO)

        return ResponseEntity
            .ok()
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$name")
            .contentLength(resource.size.toLong())
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .body(resource)
    }
}