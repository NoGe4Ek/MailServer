package com.poly.intelligentmessaging.mailserver.domain.dto

data class NewAttributeDTO(
    val name: String? = null,
    val groupName: String? = null,
    val studentsId: List<String>? = null
)