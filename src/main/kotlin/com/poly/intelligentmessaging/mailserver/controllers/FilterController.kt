package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.services.FilterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/filters")
class FilterController {

    @Autowired
    val filterService: FilterService? = null

    @PostMapping("/getFilters", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getFilters(@RequestBody staffDTO: StaffDTO): ResponseEntity<Set<FiltersDTO>> {
        return ResponseEntity(filterService!!.getFilters(staffDTO.id!!, false), HttpStatus.OK)
    }

    @PostMapping("/getFiltersShort")
    fun getFiltersShort(@RequestBody staffDTO: StaffDTO): ResponseEntity<Set<FiltersDTO>> {
        return ResponseEntity(filterService!!.getFilters(staffDTO.id!!, true), HttpStatus.OK)
    }

    @PostMapping("/createFilter", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createFilter(@RequestBody newFilterDTO: NewFilterDTO): ResponseEntity<NewFilterDTO> {
        return ResponseEntity(filterService!!.createFilter(newFilterDTO), HttpStatus.CREATED)
    }

    @PostMapping("/updateFilter", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateFilter(@RequestBody newFilterDTO: NewFilterDTO): ResponseEntity<NewFilterDTO> {
        return ResponseEntity(filterService!!.updateFilter(newFilterDTO), HttpStatus.CREATED)
    }

    @PostMapping("/deleteFilter", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteFilter(@RequestBody filterIdDTO: FilterIdDTO): ResponseEntity<FilterIdDTO> {
        return ResponseEntity(filterService!!.deleteFilter(filterIdDTO), HttpStatus.OK)
    }

    @PostMapping("/getEmails", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getEmails(@RequestBody filterIdDTO: FilterIdDTO): ResponseEntity<FilterIdDTO> {
        return ResponseEntity(filterService!!.getEmails(filterIdDTO), HttpStatus.OK)
    }

    @PostMapping("/share", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun shareFilter(@RequestBody shareDTO: ShareDTO): ResponseEntity<ShareDTO> {
        return ResponseEntity(filterService!!.shareFilter(shareDTO), HttpStatus.OK)
    }

    @PostMapping("/calculate", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun calculate(@RequestBody expressionDTO: ExpressionDTO): ResponseEntity<ComputedExpressionDTO> {
        return ResponseEntity(filterService!!.calculateExpression(expressionDTO), HttpStatus.OK)
    }

    @PostMapping("/getFilterById", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAttributeById(@RequestBody filterIdDTO: FilterIdDTO): ResponseEntity<FiltersDTO> {
        return ResponseEntity(filterService!!.getFilterById(filterIdDTO), HttpStatus.OK)
    }
}
