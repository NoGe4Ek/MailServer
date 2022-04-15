package com.poly.intelligentmessaging.mailserver.domain.dto

data class NewAttributeDTO(
    val idAttribute: String? = null,
    val name: String? = null,
    val groupName: String? = null,
    val expression: String? = null,
    val studentsId: MutableList<String>? = mutableListOf()
)