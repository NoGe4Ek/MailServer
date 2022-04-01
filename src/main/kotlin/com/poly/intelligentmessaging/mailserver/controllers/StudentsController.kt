package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.StudentsDTO
import com.poly.intelligentmessaging.mailserver.services.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/students")
class StudentsController {

    @Autowired
    var studentService: StudentService? = null

    @GetMapping("/getAll")
    fun getAllStudents(): ResponseEntity<List<StudentsDTO>> {
        return ResponseEntity(studentService!!.getAllStudents(), HttpStatus.OK)
    }
}