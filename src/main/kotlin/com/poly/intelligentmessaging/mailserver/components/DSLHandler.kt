package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.domain.dto.ComputedExpressionDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.StudentsDTO
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.exceptions.ValidationException
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class DSLHandler {

    @Autowired
    private val studentRepository: StudentRepository? = null

    @Autowired
    private val staffRepository: StaffRepository? = null

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

    fun priority(token: String) = when (token) {
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

    private fun getSampleStudentsByFunction(function: String, students: Set<StudentModel>): Set<StudentModel> {
        var (groupAttribute, args) = function.split("[")
        args = args.replace("]", "")
        if (groupAttribute.contains('_')) {
            groupAttribute = groupAttribute.replace("_", " ")
        }
        if (args.contains('_')) {
            args = args.replace("_", " ")
        }
        return students.filter {
            val needsAttribute = it.attributes!!.find { attribute ->
                attribute.name!!.lowercase() == args && attribute.group!!.name!!.lowercase() == groupAttribute
            }
            needsAttribute != null
        }.toSet()
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

    private fun validationFunction(token: String, groupAttributes: Set<GroupAttributesModel>): Boolean {
        val partsFunction = token.split("[")
        var groupAttribute = partsFunction[0]
        if (groupAttribute.contains('_')) {
            groupAttribute = groupAttribute.replace("_", " ")
        }
        var args = partsFunction[1].replace("]", "")
        if (args.contains('_')) {
            args = args.replace("_", " ")
        }
        val findGroupAttribute = groupAttributes.find { it.name!!.lowercase() == groupAttribute.lowercase() }
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
}
