package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.StaffDTO
import com.poly.intelligentmessaging.mailserver.services.StaffService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/staff")
class StaffController {

    @Autowired
    val staffService: StaffService? = null

    @GetMapping("/getAll")
    fun getStaff(): ResponseEntity<MutableList<StaffDTO>> {
        return ResponseEntity(staffService!!.getStaff(), HttpStatus.OK)
    }
}