package com.poly.intelligentmessaging.mailserver.domain.projections

interface StudentProjection {
    fun getId(): String
    fun getName(): String
    fun getEmail(): String
    fun getGroupAttributeName(): String
    fun getAttributeName(): String
}
