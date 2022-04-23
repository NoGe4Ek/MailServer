package com.poly.intelligentmessaging.mailserver.domain.dto

data class NewStaffDTO(
    val lastName: String? = null,
    val firstName: String? = null,
    val patronymic: String? = null,
    val email: String? = null,
    val department: String? = null,
    val highSchool: String? = null,
)
