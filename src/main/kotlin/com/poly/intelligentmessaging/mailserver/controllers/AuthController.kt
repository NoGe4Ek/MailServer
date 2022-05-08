package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.AuthRequestDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.AuthResponseDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.NewStaffDTO
import com.poly.intelligentmessaging.mailserver.services.AuthService
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private val authService: AuthService? = null

    @PostMapping("/check", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun check(@RequestBody authRequestDTO: AuthRequestDTO): ResponseEntity<AuthResponseDTO> {
        val response = authService!!.check(authRequestDTO)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/reset", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun reset(@RequestBody authRequestDTO: AuthRequestDTO): ResponseEntity<AuthRequestDTO> {
        return ResponseEntity(authService!!.reset(authRequestDTO), HttpStatus.OK)
    }

    @PostMapping("/getAccess", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAccess(@RequestBody newStaffDTO: NewStaffDTO): ResponseEntity<Map<String, String>> {
        return ResponseEntity(authService!!.getAccess(newStaffDTO), HttpStatus.OK)
    }
}
