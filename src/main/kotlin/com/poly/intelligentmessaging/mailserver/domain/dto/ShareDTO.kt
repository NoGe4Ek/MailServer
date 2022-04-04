package com.poly.intelligentmessaging.mailserver.domain.dto

data class ShareDTO(
    val id: String? = null,
    val staffIds: MutableList<String>? = mutableListOf()
)
