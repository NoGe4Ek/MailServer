package com.poly.intelligentmessaging.mailserver.domain.dto

data class NewTokenDTO(
    val status: String,
    val token: String? = null
)