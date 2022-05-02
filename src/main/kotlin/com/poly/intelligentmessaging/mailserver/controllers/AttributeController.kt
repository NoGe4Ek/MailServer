package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.projections.GroupNameProjection
import com.poly.intelligentmessaging.mailserver.services.AttributeService
import com.poly.intelligentmessaging.mailserver.services.GroupAttributesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/attributes")
class AttributeController {

    @Autowired
    val groupAttributesService: GroupAttributesService? = null

    @Autowired
    val attributesService: AttributeService? = null

    @PostMapping("/getGroupAttributes", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGroupAttributes(@RequestBody staffDTO: StaffDTO): ResponseEntity<MutableSet<GroupAttributeDTO>> {
        return ResponseEntity(groupAttributesService!!.getGroupAttributes(staffDTO.id!!), HttpStatus.OK)
    }

    @PostMapping("/getAttributes", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAttributes(@RequestBody staffDTO: StaffDTO): ResponseEntity<Set<AttributesDTO>> {
        return ResponseEntity(attributesService!!.getAttributes(staffDTO.id!!), HttpStatus.OK)
    }

    @PostMapping("/getAttributesCurrentStaff", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAttributesCurrentStaff(@RequestBody staffDTO: StaffDTO): ResponseEntity<Set<AttributesDTO>> {
        return ResponseEntity(attributesService!!.getAttributesCurrentStaff(staffDTO.id!!), HttpStatus.OK)
    }

    @PostMapping("/getGroupNames", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGroupNames(@RequestBody staffDTO: StaffDTO): ResponseEntity<MutableList<GroupNameProjection>> {
        return ResponseEntity(groupAttributesService!!.getGroupNames(staffDTO.id!!), HttpStatus.OK)
    }

    @PostMapping("/getGroupNamesCurrentStaff", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGroupNamesCurrentStaff(@RequestBody staffDTO: StaffDTO): ResponseEntity<MutableList<GroupNameProjection>> {
        return ResponseEntity(groupAttributesService!!.getGroupNamesCurrentStaff(staffDTO.id!!), HttpStatus.OK)
    }

    @PostMapping("/createGroupName", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createGroupName(@RequestBody groupAttributeNameDTO: GroupAttributeNameDTO): ResponseEntity<GroupAttributeNameDTO> {
        return ResponseEntity(groupAttributesService!!.createGroupName(groupAttributeNameDTO), HttpStatus.CREATED)
    }

    @PostMapping("/createAttribute", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createAttribute(@RequestBody newAttributeDTO: NewAttributeDTO): ResponseEntity<NewAttributeDTO> {
        return ResponseEntity(attributesService!!.createAttribute(newAttributeDTO), HttpStatus.CREATED)
    }

    @PostMapping("/updateAttribute", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAttribute(@RequestBody newAttributeDTO: NewAttributeDTO): ResponseEntity<NewAttributeDTO> {
        return ResponseEntity(attributesService!!.updateAttribute(newAttributeDTO), HttpStatus.CREATED)
    }

    @PostMapping("/deleteAttribute", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteAttribute(@RequestBody attributeIdDTO: AttributeIdDTO): ResponseEntity<AttributeIdDTO> {
        return ResponseEntity(attributesService!!.deleteAttribute(attributeIdDTO), HttpStatus.OK)
    }

    @PostMapping("/deleteGroupAttribute", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteGroupAttribute(@RequestBody groupAttributeIdDTO: GroupAttributeIdDTO): ResponseEntity<GroupAttributeIdDTO> {
        return ResponseEntity(groupAttributesService!!.deleteGroupAttribute(groupAttributeIdDTO), HttpStatus.OK)
    }

    @PostMapping("/share", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun shareAttribute(@RequestBody shareDTO: ShareDTO): ResponseEntity<ShareDTO> {
        return ResponseEntity(attributesService!!.shareAttribute(shareDTO), HttpStatus.OK)
    }

    @PostMapping("/calculate", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun calculate(@RequestBody expressionDTO: ExpressionDTO): ResponseEntity<ComputedExpressionDTO> {
        return ResponseEntity(attributesService!!.calculateExpression(expressionDTO), HttpStatus.OK)
    }

    @PostMapping("/getAttributeById", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAttributeById(@RequestBody attributeIdDTO: AttributeIdDTO): ResponseEntity<AttributesDTO> {
        return ResponseEntity(attributesService!!.getAttributeById(attributeIdDTO), HttpStatus.OK)
    }
}
