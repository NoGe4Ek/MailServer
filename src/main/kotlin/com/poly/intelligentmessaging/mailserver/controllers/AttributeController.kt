package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.projections.GroupNameProjection
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

    private val basicIdStaff = "ad7a8951-2f95-4619-802b-1285c3279623"

    //    private val currentStaff = "9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b"
    private val currentStaff = "725cee0f-7a95-4094-b19a-11b27f779490"

    @Autowired
    val groupAttributesService: GroupAttributesService? = null

    @Autowired
    val attributesService: AttributeService? = null

    @GetMapping("/getGroupAttributes")
    fun getGroupAttributes(): ResponseEntity<MutableSet<GroupAttributeDTO>> {
        return ResponseEntity(groupAttributesService!!.getGroupAttributes(currentStaff, basicIdStaff), HttpStatus.OK)
    }

    @GetMapping("/getAttributes")
    fun getAttributes(): ResponseEntity<List<AttributesDTO>> {
        return ResponseEntity(attributesService!!.getAttributes(currentStaff), HttpStatus.OK)
    }

    @GetMapping("/getAttributesCurrentStaff")
    fun getAttributesCurrentStaff(): ResponseEntity<List<AttributesDTO>> {
        return ResponseEntity(attributesService!!.getAttributesCurrentStaff(currentStaff), HttpStatus.OK)
    }

    @GetMapping("/getGroupNames")
    fun getGroupNames(): ResponseEntity<MutableList<GroupNameProjection>> {
        return ResponseEntity(groupAttributesService!!.getGroupNames(currentStaff), HttpStatus.OK)
    }

    @GetMapping("/getGroupNamesCurrentStaff")
    fun getGroupNamesCurrentStaff(): ResponseEntity<MutableList<GroupNameProjection>> {
        return ResponseEntity(groupAttributesService!!.getGroupNamesCurrentStaff(currentStaff), HttpStatus.OK)
    }

    @PostMapping("/createGroupName", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createGroupName(@RequestBody groupAttributeNameDTO: GroupAttributeNameDTO): ResponseEntity<GroupAttributeNameDTO> {
        return ResponseEntity(
            groupAttributesService!!.createGroupName(groupAttributeNameDTO, currentStaff),
            HttpStatus.CREATED
        )
    }

    @PostMapping("/createAttribute", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createAttribute(@RequestBody newAttributeDTO: NewAttributeDTO): ResponseEntity<NewAttributeDTO> {
        return ResponseEntity(attributesService!!.createAttribute(newAttributeDTO, currentStaff), HttpStatus.CREATED)
    }

    @PostMapping("/updateAttribute", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAttribute(@RequestBody newAttributeDTO: NewAttributeDTO): ResponseEntity<NewAttributeDTO> {
        return ResponseEntity(attributesService!!.updateAttribute(newAttributeDTO, currentStaff), HttpStatus.CREATED)
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
        return ResponseEntity(attributesService!!.calculateExpression(expressionDTO, currentStaff), HttpStatus.OK)
    }

    @PostMapping("/getAttributeById", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAttributeById(@RequestBody attributeIdDTO: AttributeIdDTO): ResponseEntity<AttributesDTO> {
        return ResponseEntity(attributesService!!.getAttributeById(attributeIdDTO), HttpStatus.OK)
    }
}
