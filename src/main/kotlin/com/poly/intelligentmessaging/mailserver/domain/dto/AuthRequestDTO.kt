package com.poly.intelligentmessaging.mailserver.domain.dto

data class AuthRequestDTO(
    val login: String? = null,
    val password: String? = null,
)