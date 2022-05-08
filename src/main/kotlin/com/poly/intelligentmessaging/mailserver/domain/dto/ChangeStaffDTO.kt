package com.poly.intelligentmessaging.mailserver.domain.dto

data class ChangeStaffDTO(
    val id: String? = null,
    val roles: Set<String> = mutableSetOf()
)
