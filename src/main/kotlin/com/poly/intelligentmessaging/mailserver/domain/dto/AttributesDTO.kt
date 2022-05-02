package com.poly.intelligentmessaging.mailserver.domain.dto

data class AttributesDTO(
    val id: String,
    val attributeName: String,
    val groupName: String,
    val expression: String? = null,
    val type: String,
    val link: Boolean,
    val created: String,
    val students: Set<String> = mutableSetOf(),
    val studentsDTO: MutableSet<StudentsDTO> = mutableSetOf(),
    val status: String? = null
)