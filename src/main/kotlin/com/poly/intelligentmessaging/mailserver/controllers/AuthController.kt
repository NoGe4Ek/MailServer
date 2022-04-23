package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.AuthDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.NewStaffDTO
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class AuthController {

    @PostMapping("/check", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun check(@RequestBody authDTO: AuthDTO): ResponseEntity<AuthDTO> {
        return ResponseEntity(authDTO, HttpStatus.ACCEPTED)
    }

    @PostMapping("/reset", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun reset(@RequestBody authDTO: AuthDTO): ResponseEntity<AuthDTO> {
        return ResponseEntity(authDTO, HttpStatus.OK)
    }

    @PostMapping("/getAccess", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAccess(@RequestBody newStaffDTO: NewStaffDTO): ResponseEntity<NewStaffDTO> {
        return ResponseEntity(newStaffDTO, HttpStatus.OK)
    }
}