package com.poly.intelligentmessaging.mailserver.domain

data class EmailData(
    val email: String,
    val password: String,
    val mailDirectory: String
)
