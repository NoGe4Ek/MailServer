package com.poly.intelligentmessaging.mailserver.domain.projections

interface GroupNameProjection {
    fun getIdGroupName(): String
    fun getGroupName(): String
    fun getIdStaff(): String
}