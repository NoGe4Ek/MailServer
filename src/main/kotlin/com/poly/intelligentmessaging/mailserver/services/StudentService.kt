package com.poly.intelligentmessaging.mailserver.services


import com.poly.intelligentmessaging.mailserver.domain.dto.StaffDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.StudentsDTO
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StudentService {

    @Autowired
    var studentRepository: StudentRepository? = null

    fun getAllStudents(staffDTO: StaffDTO): Set<StudentsDTO> {
        val listStudentsDTO = mutableMapOf<String, StudentsDTO>()
        val students = studentRepository!!.findAllStudents(staffDTO.id!!, BASIC_ID_STAFF)

        for (student in students) {
            val studentId = student.getId()
            val studentName = student.getName()
            val studentEmail = student.getEmail()
            val groupAttributeName = student.getGroupAttributeName()
            val attributeName = student.getAttributeName()

            if (listStudentsDTO.containsKey(studentId)) {
                if (listStudentsDTO[studentId]!!.attributes.containsKey(groupAttributeName)) {
                    listStudentsDTO[studentId]!!.attributes[groupAttributeName]!!.add(attributeName)
                } else {
                    listStudentsDTO[studentId]!!.attributes[groupAttributeName] = mutableListOf(attributeName)
                }
            } else {
                val studentDTO = StudentsDTO(studentId, studentName, studentEmail)
                studentDTO.attributes[groupAttributeName] = mutableListOf(attributeName)
                listStudentsDTO[studentId] = studentDTO
            }
        }
        return listStudentsDTO.values.toSet()
    }
}