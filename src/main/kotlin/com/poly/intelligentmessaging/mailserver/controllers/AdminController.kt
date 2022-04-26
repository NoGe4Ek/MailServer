package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.AccessDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.ResponseDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.RoleDTO
import com.poly.intelligentmessaging.mailserver.services.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdminController {

    @Autowired
    private val adminService: AdminService? = null

    @PostMapping("/setup", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun setup(@RequestBody responseDTO: ResponseDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity(adminService!!.setup(responseDTO), HttpStatus.OK)
    }

    @PostMapping("/change", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun change(@RequestBody responseDTO: ResponseDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity(adminService!!.change(responseDTO), HttpStatus.OK)
    }

    @PostMapping("/reject", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun reject(@RequestBody responseDTO: ResponseDTO): ResponseEntity<ResponseDTO> {
        return ResponseEntity(adminService!!.reject(responseDTO), HttpStatus.OK)
    }

    @GetMapping("/getAccessList")
    fun getAccessList(): ResponseEntity<Set<AccessDTO>> {
        return ResponseEntity(adminService!!.getAccessList(), HttpStatus.OK)
    }

    @GetMapping("/getRoles")
    fun getRoles(): ResponseEntity<Set<RoleDTO>> {
        return ResponseEntity(adminService!!.getRoles(), HttpStatus.OK)
    }
}
