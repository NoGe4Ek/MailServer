package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.domain.dto.ComputedExpressionDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.StudentsDTO
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.domain.projections.AttributeProjection
import com.poly.intelligentmessaging.mailserver.exceptions.ValidationException
import com.poly.intelligentmessaging.mailserver.repositories.AttributeRepository
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Component
class DSLHandler {

    @Autowired
    private val studentRepository: StudentRepository? = null

    @Autowired
    private val attributeRepository: AttributeRepository? = null

    @Autowired
    private val groupAttributesRepository: GroupAttributesRepository? = null

    fun getComputedExpression(expression: String, currentIdStaff: String, basicIdStaff: String): ComputedExpressionDTO {
        val groupAttributes = groupAttributesRepository!!.findAllByStaffIdOrStaffId(
            UUID.fromString(currentIdStaff),
            UUID.fromString(basicIdStaff)
        )
        val (status, students) = calculateExpression(expression, groupAttributes)
        val studentsDTO = mutableSetOf<StudentsDTO>()
        for (student in students) {
            val person = student.person!!
            val fullName = "${person.lastName} ${person.firstName} ${person.patronymic}"
            studentsDTO.add(StudentsDTO(student.id.toString(), fullName, person.email!!))
        }
        return ComputedExpressionDTO(status, studentsDTO)
    }

    fun getStatus(
        created: LocalDateTime,
        expression: String,
        idStaff: String
    ): String {
        val attributes = attributeRepository!!.getAttributes(idStaff)
        var (status, listAttributeFromExpression) = getAttributesFromExpression(expression, attributes)
        if (status == "error") return status
        val finder = listAttributeFromExpression.find {
            created < Timestamp.valueOf(it.getCreated()).toLocalDateTime()
        }
        if (finder != null) status = "warning"
        return status
    }

    fun getAttributesFromExpression(expression: String): MutableMap<String, MutableSet<String>> {
        val groupToAttributes = mutableMapOf<String, MutableSet<String>>()
        var position = 0
        while (position < expression.length) {
            val (token, newPosition) = getToken(position, expression)
            if (token.length == 1) continue
            val (group, args) = getGroupAndAttr(token)
            if (groupToAttributes.containsKey(group)) groupToAttributes[group]!!.add(args)
            else groupToAttributes[group] = mutableSetOf(args)
            position = newPosition
        }
        return groupToAttributes
    }

    private fun getAttributesFromExpression(
        expr: String,
        attributes: List<AttributeProjection>
    ): Pair<String, Set<AttributeProjection>> {
        val expression = expr.replace(Regex("""\s+"""), "")
        val attributesFromExpression = mutableSetOf<AttributeProjection>()
        var position = 0
        while (position < expression.length) {
            val (token, newPosition) = getToken(position, expression)
            if (token.length > 1) {
                val groupAndAttribute = token.split("[")
                val group = groupAndAttribute[0]
                val attribute = groupAndAttribute[1].replace("]", "")
                val finder = attributes.find {
                    val attributeCondition =
                        it.getAttributeName().lowercase().replace("\\s+".toRegex(), "_") == attribute
                    val groupCondition = it.getGroupName().lowercase().replace("\\s+".toRegex(), "_") == group
                    attributeCondition && groupCondition
                } ?: return "error" to setOf()
                attributesFromExpression.add(finder)
            }
            position = newPosition
        }
        return "success" to attributesFromExpression
    }

    fun validationExpression(expression: String, groupAttributes: Set<GroupAttributesModel>): Boolean {
        var position = 0

        var openBracket = 0
        var closeBracket = 0

        var counterFunction = 0
        var counterOperators = 0
        while (position < expression.length) {
            val (token, newPosition) = getToken(position, expression)
            when {
                token.length == 1 -> {
                    if (token.matches(Regex("""[&|-]"""))) {
                        val isError = checkIncorrectOperatorPosition(expression, token, position)
                        if (isError) throw ValidationException("incorrect operator position: $expression")
                        counterOperators++
                    }
                    if (token == "-" && (position == 0 || expression[position - 1] == '(')) counterFunction++
                    if (token == "(") openBracket++
                    else if (token == ")") closeBracket++
                }
                token.length > 1 && token.matches("""[a-zA-Zа-яА-Я_0-9]+\[[a-zA-Zа-яА-Я0-9_/\\]+]""".toRegex()) -> {
                    if (!validationFunction(token, groupAttributes)) {
                        throw ValidationException("incorrect token: $token")
                    } else counterFunction++
                }
                else -> throw ValidationException("incorrect token: $token  ->  { $expression }")
            }
            position = newPosition
        }
        if (openBracket != closeBracket) {
            throw ValidationException("incorrect brackets in expression: $expression")
        }
        if (counterFunction - counterOperators != 1) throw ValidationException("extra operator: $expression")
        return true
    }

    fun toRPN(expression: String): List<String> {
        val result = mutableListOf<String>()
        val stack = Stack<String>()
        var functionCounter = 0

        var position = 0

        while (position < expression.length) {
            val (token, newPosition) = getToken(position, expression)
            val priority = priority(token)
            when {
                priority == 0 -> {
                    result.add(token)
                    functionCounter++
                }
                priority == 1 -> stack.push(token)
                priority > 1 -> {
                    if (priority == 2 && (position == 0 || expression[position - 1] == '(')) {
                        result.add("ALL")
                    }
                    if (stack.isEmpty()) stack.push(token)
                    else {
                        while (!stack.isEmpty() && priority(stack.peek()) >= priority) result.add(stack.pop())
                        stack.push(token)
                    }
                }
                priority == -1 -> {
                    while (priority(stack.peek()) != 1) result.add(stack.pop())
                    stack.pop()
                }
            }
            position = newPosition
        }
        while (!stack.isEmpty()) result.add(stack.pop())
        return result.toList()
    }

    private fun calculateExpression(
        expression: String,
        groupAttributes: Set<GroupAttributesModel>
    ): Pair<String, Set<StudentModel>> {
        return try {
            val expressionWithoutSpace = expression.replace(Regex("""\s+"""), "")
            validationExpression(expressionWithoutSpace, groupAttributes)
            val rpn = toRPN(expressionWithoutSpace)
            val result = calculateRPN(rpn)
            val status = if (result.isEmpty()) "warning" else "success"
            status to result
        } catch (e: ValidationException) {
            "error" to setOf()
        }
    }

    private fun calculateRPN(rpn: List<String>): Set<StudentModel> {
        val stack = Stack<Set<StudentModel>>()
        val students = studentRepository!!.findAll().toSet()
        for (token in rpn) {
            if (token.length > 1) {
                if (token == "ALL") stack.push(students)
                else stack.push(getSampleStudentsByFunction(token, students))
            } else when (token) {
                "&" -> stack.push(stack.pop().intersect(stack.pop()))
                "|" -> stack.push(stack.pop() + stack.pop())
                "-" -> {
                    val sample1 = stack.pop()
                    val sample2 = stack.pop()
                    val newSample = sample2 - sample1
                    stack.push(newSample)
                }
            }
        }
        return stack.pop()
    }

    private fun priority(token: String) = when (token) {
        "&" -> 4
        "|" -> 3
        "-" -> 2
        "(" -> 1
        ")" -> -1
        else -> 0
    }

    private fun getToken(currentPosition: Int, expression: String): Pair<String, Int> {
        var position = currentPosition
        var token = ""
        when (expression[position]) {
            '|', '&', '(', ')', '-' -> token = expression[currentPosition].toString()
            else -> while (expression[position] != ']') {
                if (position == expression.length - 1 && expression[position] != ']') {
                    throw ValidationException("incorrect expression: $expression")
                }
                position++
            }
        }
        position++
        if (position != currentPosition) {
            token = expression.substring(currentPosition, position)
        }
        return token to position
    }

    private fun getSampleStudentsByFunction(token: String, students: Set<StudentModel>): Set<StudentModel> {
        val (group, args) = getGroupAndAttr(token)
        return students.filter {
            val needsAttribute = it.attributes!!.find { attribute ->
                attribute.name!!.lowercase() == args && attribute.group!!.name!!.lowercase() == group
            }
            needsAttribute != null
        }.toSet()
    }

    private fun validationFunction(token: String, groupAttributes: Set<GroupAttributesModel>): Boolean {
        val (group, args) = getGroupAndAttr(token)
        val findGroupAttribute = groupAttributes.find { it.name!!.lowercase() == group.lowercase() }
        if (findGroupAttribute != null) {
            if (findGroupAttribute.attributes!!.find { it.name!!.lowercase() == args.lowercase() } != null) {
                return true
            }
        } else return false
        return false
    }

    private fun checkIncorrectOperatorPosition(expression: String, token: String, position: Int): Boolean {
        return if (token.matches(Regex("""[&|]"""))) {
            when {
                position == 0 -> true
                expression[position - 1].toString().matches(Regex("""[(&|-]""")) -> true
                position == expression.length - 1 -> true
                expression[position + 1].toString().matches(Regex("""[)&|-]""")) -> true
                else -> false
            }
        } else {
            when {
                position == expression.length - 1 -> true
                expression[position + 1] == ')' -> true
                else -> false
            }
        }
    }

    private fun getGroupAndAttr(token: String): Pair<String, String> {
        var (group, args) = token.split("[")
        args = args.replace("]", "")
        if (group.contains('_')) {
            group = group.replace("_", " ")
        }
        if (args.contains('_')) {
            args = args.replace("_", " ")
        }
        return group to args
    }
}
