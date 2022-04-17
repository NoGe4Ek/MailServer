package com.poly.intelligentmessaging.mailserver

import com.poly.intelligentmessaging.mailserver.components.DSLHandler
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.exceptions.ValidationException
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import kotlin.test.assertTrue


@SpringBootTest
class DSLHandlerTests {

    @Autowired
    private val dslHandler: DSLHandler? = null

    @Autowired
    private val groupAttributesRepository: GroupAttributesRepository? = null

    @Autowired
    private val staffRepository: StaffRepository? = null

    //    private val currentIdStaff = "9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b"
    private val currentIdStaff = "725cee0f-7a95-4094-b19a-11b27f779490"
    private val basicIdStaff = "ad7a8951-2f95-4619-802b-1285c3279623"

    private fun getGroupAttributes(): Set<GroupAttributesModel> {
//        val currentStaff = staffRepository!!.findById(UUID.fromString(currentIdStaff)).get()
//        val basicStaff = staffRepository.findById(UUID.fromString(basicIdStaff)).get()
        return groupAttributesRepository!!.findAllByStaffIdOrStaffId(
            UUID.fromString(currentIdStaff),
            UUID.fromString(basicIdStaff)
        )
    }

    private val listCorrectExpressions = listOf(
        "(номер_группы[3530901/80203] | номер_группы[3530901/80203]) & финансирование[бюджет]",
        "(номер_группы[3530901/80203]) & финансирование[бюджет]",
        "(номер_группы[3530901/80203]) & (финансирование[бюджет])",
        "((номер_группы[3530901/80203]) & (финансирование[бюджет]))",
        "(((номер_группы[3530901/80203])) & (финансирование[бюджет]))",
        "(номер_группы[3530901/80203] & номер_группы[3530901/80203]) - номер_группы[3530901/80203]",
        "-номер_группы[3530901/80203] & ( - номер_группы[3530901/80203] & номер_группы[3530901/80203]) - номер_группы[3530901/80203]",
        "номер_группы[3530901/80203] - (номер_группы[3530901/80203] | номер_группы[3530901/80203])",
        "-номер_группы[3530901/80203]",
        "(номер_группы[3530901/80203] & номер_группы[3530901/80202]) & финансирование[бюджет]",
    )
    private val listIncorrectExpressions = listOf(
        "номер_группы",
        "(номер_группы[3530901/80203]",
        "номер_группы[3530901/80203])",
        "номергруппы[3530901/80203]",
        "номер_группы[3530901/80203] &",
        "номер_группы[3530901/80203] & номер_группы[3530901/80203] номер_группы[3530901/80203]",
        "(номер_группы[3530901/80203] & номер_группы[3530901/80203]) номер_группы[3530901/80203]",
        "-номер_группы[3530901/80203] &",
        "& (номер_группы[3530901/80203])",
        "(номер_группы[3530901/80203]-)",
        "номер_группы[3530901/80203] & & номер_группы[3530901/80203] номер_группы[3530901/80203]",
        "(номер_группы[3530901/80203] &) (номер_группы[3530901/80203]) & номер_группы[3530901/80203]",
        "номер_группы[3530901/80203] & (& номер_группы[3530901/80203]) номер_группы[3530901/80203]",
        "номер_группы[3530901/80203] - (номер_группы[3530901/80203] | номер_группы[3530901/80203]-)",
        "номер_группы[3530901/80203] -"
    )

    @Test
    fun validationExpressionTests() {
        requireNotNull(dslHandler)
        val groupAttributes = getGroupAttributes()

        for (expression in listCorrectExpressions) {
            val expressionWithoutSpace = expression.replace(Regex("""\s+"""), "")
            assertTrue(dslHandler.validationExpression(expressionWithoutSpace, groupAttributes))
        }
        for (expression in listIncorrectExpressions) {
            val expressionWithoutSpace = expression.replace(Regex("""\s+"""), "")
            assertThrows(ValidationException::class.java) {
                dslHandler.validationExpression(expressionWithoutSpace, groupAttributes)
            }
        }
    }

    @Test
    fun toRPNTests() {
        requireNotNull(dslHandler)
        listCorrectExpressions.forEach { expression ->
            val expressionWithoutSpace = expression.replace(Regex("""\s+"""), "")
            println(dslHandler.toRPN(expressionWithoutSpace))
        }
    }
}
