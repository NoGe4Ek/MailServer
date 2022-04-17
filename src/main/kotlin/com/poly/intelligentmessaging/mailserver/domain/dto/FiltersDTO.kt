package com.poly.intelligentmessaging.mailserver.domain.dto

data class FiltersDTO(
    val id: String,
    val filterName: String,
    val mail: String,
    val expression: String? = null,
    val type: String,
    val mode: String,
    val created: String,
    val mailCounter: Int? = null,
    val students: MutableList<String> = mutableListOf()
)
