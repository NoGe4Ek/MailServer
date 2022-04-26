package com.poly.intelligentmessaging.mailserver.domain.dto

data class NotificationDTO(
    val idNotification: String,
    val idOwner: String,
    val fullNameOwner: String,
    val type: String,
    val name: String,
    val date: String
)