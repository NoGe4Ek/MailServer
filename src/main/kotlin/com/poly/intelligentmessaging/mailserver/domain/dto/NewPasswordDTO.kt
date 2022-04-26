package com.poly.intelligentmessaging.mailserver.domain.dto

data class NewPasswordDTO(
    val idStaff: String? = null,
    val oldPassword: String? = null,
    val newPassword: String? = null
)
