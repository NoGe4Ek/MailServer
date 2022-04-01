package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.AttributesDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeNameDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.NewAttributeDTO
import com.poly.intelligentmessaging.mailserver.domain.models.AttributeModel
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.services.AttributeService
import com.poly.intelligentmessaging.mailserver.services.GroupAttributesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/attributes")
class AttributeController {

    @Autowired
    val groupAttributesService: GroupAttributesService? = null

    @Autowired
    val attributesService: AttributeService? = null

    @GetMapping("/getGroupAttributes")
    fun getGroupAttributes(): ResponseEntity<MutableList<GroupAttributeDTO>> {
        return ResponseEntity(groupAttributesService!!.getGroupAttributes(), HttpStatus.OK)
    }

    @GetMapping("/getAttributes")
    fun getAttributes(): ResponseEntity<List<AttributesDTO>> {
        return ResponseEntity(attributesService!!.getAttributes(), HttpStatus.OK)
    }

    @GetMapping("/getGroupNames")
    fun getGroupNames(): ResponseEntity<List<String>> {
        return ResponseEntity(groupAttributesService!!.getGroupNames(), HttpStatus.OK)
    }

    @PostMapping("/createGroupName", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createGroupName(@RequestBody groupAttributeNameDTO: GroupAttributeNameDTO): ResponseEntity<GroupAttributesModel> {
        return ResponseEntity(
            groupAttributesService!!.createGroupName(groupAttributeNameDTO.groupName!!),
            HttpStatus.CREATED
        )
    }

    @PostMapping("/createAttribute", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createAttribute(@RequestBody newAttributeDTO: NewAttributeDTO): ResponseEntity<AttributeModel> {
        return ResponseEntity(attributesService!!.createAttribute(newAttributeDTO), HttpStatus.CREATED)
    }

}