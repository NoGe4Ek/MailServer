package com.poly.intelligentmessaging.mailserver.domain.dto

data class NotificationResponseDTO(
    val idNotification: String,
    val type: String? = null
)