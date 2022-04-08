package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class DSLHandler {

    @Autowired
    private val studentRepository: StudentRepository? = null

    fun getStudentsByExpression(expression: String): Set<StudentModel> {
        val (rpn, functionCounter) = toRPN(expression)
        return calculateRPN(rpn, functionCounter)
    }

    private fun calculateRPN(rpn: List<String>, functionCounter: Int): Set<StudentModel> {
        val stack = Stack<Set<StudentModel>>()
        val students = studentRepository!!.findAll().toSet()
        for (token in rpn) {
            if (token.length > 1) {
                stack.push(getSampleStudentsByFunction(token, students))
            } else when (token) {
                "&" -> {
                    val newSample = mergeStudents(stack.pop(), stack.pop())
                    stack.push(newSample)
                }
                "|" -> {
                    val newSample = intersectionStudents(stack.pop(), stack.pop())
                    stack.push(newSample)
                }
                "-" -> {
                    val sample1 = stack.pop()
                    val sample2 = if (stack.isEmpty()) students else stack.pop()
                    val newSample = negationStudents(sample1, sample2.toSet())
                    stack.push(newSample)
                }
            }
        }
        return stack.pop()
    }

    private fun toRPN(expression: String): Pair<List<String>, Int> {

        val result = mutableListOf<String>()
        val stack = Stack<String>()
        var functionCounter = 0

        var position = 0

        while (position < expression.length) {
            val (token, newPosition) = getToken(position, expression)
            if (priority(token) == 0) {
                result.add(token)
                position = newPosition - 1
                functionCounter++
            } else if (priority(token) == 1) stack.push(token)
            else if (priority(token) > 1) {
                if (stack.isEmpty()) stack.push(token)
                else {
                    while (!stack.isEmpty() && priority(stack.peek()) >= priority(token)) result.add(stack.pop())
                    stack.push(token)
                }
            } else if (priority(token) == -1) {
                while (priority(stack.peek()) != 1) result.add(stack.pop())
                stack.pop()
            }
            position++
        }
        while (!stack.isEmpty()) result.add(stack.pop())
        return result.toList() to functionCounter
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
            else -> while (expression[position] != ']') position++
        }
        if (position != currentPosition) {
            position++
            token = expression.substring(currentPosition, position)
        }
        return token to position
    }

    private fun negationStudents(sample1: Set<StudentModel>, sample2: Set<StudentModel>): Set<StudentModel> {
        return sample2 - sample1
    }

    private fun intersectionStudents(sample1: Set<StudentModel>, sample2: Set<StudentModel>): Set<StudentModel> {
        return sample1.intersect(sample2)
    }

    private fun mergeStudents(sample1: Set<StudentModel>, sample2: Set<StudentModel>): Set<StudentModel> {
        return sample1 + sample2
    }

    private fun getSampleStudentsByFunction(function: String, students: Set<StudentModel>): Set<StudentModel> {
        val partsFunction = function.split("[")
        val groupAttribute = partsFunction[0]
        val args = partsFunction[1].replace("]", "")
        return students.filter {
            val needsAttribute = it.attributes!!.find { attribute ->
                attribute.name == args && attribute.group!!.name == groupAttribute
            }
            needsAttribute != null
        }.toSet()
    }
}