package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.ExcelDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/excel")
class ExcelController {

    @PostMapping("/download")
    fun downloadExcel(@RequestBody excelDTO: ExcelDTO): ResponseEntity<ExcelDTO> {
        return ResponseEntity(excelDTO, HttpStatus.OK)
    }
}