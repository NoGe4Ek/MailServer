package com.poly.intelligentmessaging.mailserver.domain

data class StudentExcel(
    val lastName: String,
    val firstName: String,
    val patronymic: String,
    val email: String,
    var attributes: MutableMap<String, String> = mutableMapOf()
)
