package com.poly.intelligentmessaging.mailserver.domain.dto

data class AuthResponseDTO(
    val status: Boolean,
    val idStaff: String? = null,
    val token: String? = null,
    val fullName: String? = null,
    val email: String? = null,
    val roles: Set<String>? = null
)
