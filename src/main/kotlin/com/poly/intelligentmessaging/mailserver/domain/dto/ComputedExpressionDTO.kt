package com.poly.intelligentmessaging.mailserver.domain.dto

data class ComputedExpressionDTO(
    val status: String,
    val students: MutableSet<StudentsDTO> = mutableSetOf()
)
