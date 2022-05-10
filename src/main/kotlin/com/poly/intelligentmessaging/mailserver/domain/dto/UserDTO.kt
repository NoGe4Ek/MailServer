package com.poly.intelligentmessaging.mailserver.domain.dto

data class UserDTO(
    val id: String,
    val lastName: String,
    val firstName: String,
    val patronymic: String,
    val email: String,
    val admin: Boolean,
    val user: Boolean,
    val date: String,
)