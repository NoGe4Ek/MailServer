package com.poly.intelligentmessaging.mailserver.domain.dto

data class NewFilterDTO(
    val idFilter: String? = null,
    val name: String? = null,
    val mailOption: String? = null,
    val studentsId: MutableList<String>? = mutableListOf()
)