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

    private val currentStaff = "9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b"
//    private val currentStaff = "725cee0f-7a95-4094-b19a-11b27f779490"

    private val basicStaff = "ad7a8951-2f95-4619-802b-1285c3279623"

    @Autowired
    val staffService: StaffService? = null

    @GetMapping("/getAll")
    fun getStaff(): ResponseEntity<MutableList<StaffDTO>> {
        return ResponseEntity(staffService!!.getStaff(currentStaff, basicStaff), HttpStatus.OK)
    }
}