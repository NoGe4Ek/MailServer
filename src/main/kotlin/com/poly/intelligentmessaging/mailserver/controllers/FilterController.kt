package com.poly.intelligentmessaging.mailserver.controllers

import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.services.FilterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/filters")
class FilterController {

    //    private val currentStaff = "9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b"
    private val currentStaff = "725cee0f-7a95-4094-b19a-11b27f779490"

    @Autowired
    val filterService: FilterService? = null

    @GetMapping("/getFilters")
    fun getFilters(): ResponseEntity<List<FiltersDTO>> {
        return ResponseEntity(filterService!!.getFilters(currentStaff, false), HttpStatus.OK)
    }

    @GetMapping("/getFiltersShort")
    fun getFiltersShort(): ResponseEntity<List<FiltersDTO>> {
        return ResponseEntity(filterService!!.getFilters(currentStaff, true), HttpStatus.OK)
    }

    @PostMapping("/createFilter", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createFilter(@RequestBody newFilterDTO: NewFilterDTO): ResponseEntity<NewFilterDTO> {
        return ResponseEntity(filterService!!.createFilter(newFilterDTO, currentStaff), HttpStatus.CREATED)
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
        return ResponseEntity(filterService!!.calculateExpression(expressionDTO, currentStaff), HttpStatus.OK)
    }
}
