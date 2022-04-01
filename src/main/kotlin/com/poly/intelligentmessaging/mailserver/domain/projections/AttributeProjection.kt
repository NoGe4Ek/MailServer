package com.poly.intelligentmessaging.mailserver.domain.projections

interface AttributeProjection {
    fun getId(): String
    fun getAttributeName(): String
    fun getGroupName(): String
    fun getExpression(): String?
    fun getCreated(): String
    fun getStudentId(): String
}