package com.poly.intelligentmessaging.mailserver.domain.projections

interface GroupAttributeProjection {
    fun getId(): String
    fun getGroupName(): String
    fun getAttributes(): String
}