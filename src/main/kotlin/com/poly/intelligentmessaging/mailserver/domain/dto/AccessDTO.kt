package com.poly.intelligentmessaging.mailserver.domain.dto

data class AccessDTO(
    val idRequest: String,
    val idStaff: String? = null,
    val fullName: String,
    val email: String,
    val department: String,
    val highSchool: String,
    val role: String? = null,
    val date: String
)
