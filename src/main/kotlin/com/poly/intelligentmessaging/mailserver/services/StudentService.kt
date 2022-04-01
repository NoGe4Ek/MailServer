package com.poly.intelligentmessaging.mailserver.services


import com.poly.intelligentmessaging.mailserver.domain.dto.StudentsDTO
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StudentService {

    @Autowired
    var studentRepository: StudentRepository? = null

    fun getAllStudents(): List<StudentsDTO> {
        val listStudentsDTO = mutableMapOf<String, StudentsDTO>()
        val students = studentRepository!!.findAllStudents()

        for (student in students) {
            val studentId = student.getId()
            val studentName = student.getName()
            val studentEmail = student.getEmail()
            val groupAttributeName = student.getGroupAttributeName()
            val attributeName = student.getAttributeName()

            if (listStudentsDTO.containsKey(studentId)) {
                listStudentsDTO[studentId]!!.attributes[groupAttributeName] = attributeName
            } else {
                val studentDTO = StudentsDTO(studentId, studentName, studentEmail)
                studentDTO.attributes[groupAttributeName] = attributeName
                listStudentsDTO[studentId] = studentDTO
            }
        }
        return listStudentsDTO.values.toList()
    }
}