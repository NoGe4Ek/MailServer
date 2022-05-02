package com.poly.intelligentmessaging.mailserver

import com.poly.intelligentmessaging.mailserver.repositories.AttributeRepository
import com.poly.intelligentmessaging.mailserver.services.AttributeService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class AttributeTests {

    @Autowired
    private val attributeService: AttributeService? = null

    @Autowired
    private val attributeRepository: AttributeRepository? = null

    @Test
    fun cascadeOpeningTest() {
        requireNotNull(attributeService)
        requireNotNull(attributeRepository)
        val attr = attributeRepository.findById(UUID.fromString("c10ed248-2b05-4d26-8263-aea9b4629ab5")).get()
        val attrs = attributeService.cascadeOpeningExpression(setOf(attr))
        for (i in attrs) {
            println(i)
        }
    }
}