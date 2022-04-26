package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.services.StaffService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/staff")
class StaffController {

    @Autowired
    val staffService: StaffService? = null

    @PostMapping("/getAll")
    fun getStaff(@RequestBody staffDTO: StaffDTO): ResponseEntity<MutableList<StaffDTO>> {
        return ResponseEntity(staffService!!.getStaff(staffDTO), HttpStatus.OK)
    }

    @PostMapping("/changePassword", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun changePassword(@RequestBody newPasswordDTO: NewPasswordDTO): ResponseEntity<NewTokenDTO> {
        return ResponseEntity(staffService!!.changePassword(newPasswordDTO), HttpStatus.OK)
    }

    @PostMapping("/changeAccess", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun changeAccess(@RequestBody staffDTO: StaffDTO): ResponseEntity<String> {
        staffService!!.changeAccess(staffDTO)
        return ResponseEntity("Request was saved", HttpStatus.OK)
    }

    @PostMapping("/getNotifications", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getNotifications(@RequestBody staffDTO: StaffDTO): ResponseEntity<Set<NotificationDTO>> {
        return ResponseEntity(staffService!!.getNotifications(staffDTO), HttpStatus.OK)
    }

    @PostMapping("/acceptRequest", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun acceptRequest(@RequestBody nrDTO: NotificationResponseDTO): ResponseEntity<NotificationResponseDTO> {
        return ResponseEntity(staffService!!.acceptRequest(nrDTO), HttpStatus.OK)
    }

    @PostMapping("/rejectRequest", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun rejectRequest(@RequestBody nrDTO: NotificationResponseDTO): ResponseEntity<NotificationResponseDTO> {
        return ResponseEntity(staffService!!.rejectRequest(nrDTO), HttpStatus.OK)
    }
}
