package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.services.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class EmailController {

    @Autowired
    private val emailService: EmailService? = null

    @GetMapping("/")
    fun info(): String = "Hi! How are u? :)"

    @PostMapping("/send")
    fun sendMessage(@RequestParam message: UUID, @RequestParam sender: UUID): HttpStatus {
        if (emailService == null) return HttpStatus.INTERNAL_SERVER_ERROR
        emailService.sendEmail(message, sender)
        return HttpStatus.OK
    }

    @PostMapping("/forward")
    fun forwardMessage(@RequestParam filter: UUID, @RequestParam recipient: UUID): HttpStatus {
        if (emailService == null) return HttpStatus.INTERNAL_SERVER_ERROR
        emailService.forwardEmail(filter, recipient)
        return HttpStatus.OK
    }

    @PostMapping("/create/filter")
    fun createFilter() {
        TODO()
    }

    @PostMapping("/create/attribute")
    fun createAttribute() {
        TODO()
    }
}
