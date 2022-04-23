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

    private val currentStaff = "9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b"
//    private val currentStaff = "725cee0f-7a95-4094-b19a-11b27f779490"

    @Autowired
    var studentService: StudentService? = null

    @GetMapping("/getAll")
    fun getAllStudents(): ResponseEntity<List<StudentsDTO>> {
        return ResponseEntity(studentService!!.getAllStudents(currentStaff), HttpStatus.OK)
    }
}