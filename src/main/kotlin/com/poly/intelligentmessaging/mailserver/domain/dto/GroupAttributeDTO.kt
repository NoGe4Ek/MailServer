package com.poly.intelligentmessaging.mailserver.domain.dto

data class GroupAttributeDTO(
    val id: String = "",
    val groupName: String = "",
    val attributes: Set<String> = mutableSetOf()
)