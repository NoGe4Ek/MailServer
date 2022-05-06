package com.poly.intelligentmessaging.mailserver.domain.dto

data class StudentsDTO(
    val id: String,
    val name: String,
    val email: String,
    val attributes: Map<String, Set<String>> = mutableMapOf()
)