package com.poly.intelligentmessaging.mailserver.domain.projections

interface FilterProjection {
    fun getId(): String
    fun getFilterName(): String
    fun getEmail(): String
    fun getExpression(): String?
    fun getMode(): String
    fun getCreated(): String
    fun getIdStudent(): String
}