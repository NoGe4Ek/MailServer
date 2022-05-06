package com.poly.intelligentmessaging.mailserver.services


import com.poly.intelligentmessaging.mailserver.domain.dto.StaffDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.StudentsDTO
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class StudentService {

    @Autowired
    private val studentRepository: StudentRepository? = null

    @Autowired
    private val groupAttributesRepository: GroupAttributesRepository? = null

    fun getAllStudents(staffDTO: StaffDTO): Set<StudentsDTO> {
        val students = studentRepository!!.findAll()
        val groupAttributes = groupAttributesRepository!!.findAllByStaffIdOrStaffId(
            UUID.fromString(staffDTO.id),
            UUID.fromString(BASIC_ID_STAFF)
        )
        val studentsDTO = mutableSetOf<StudentsDTO>()
        for (student in students) {
            val person = student.person!!
            val id = student.id.toString()
            val fullName = "${person.lastName} ${person.firstName} ${person.patronymic}"
            val email = person.email!!
            val studentAttributes = getStudentAttributes(student, groupAttributes)
            studentsDTO.add(StudentsDTO(id, fullName, email, studentAttributes))
        }
        return studentsDTO.toSet()
    }

    private fun getStudentAttributes(
        student: StudentModel,
        groupAttributes: Set<GroupAttributesModel>
    ): Map<String, Set<String>> {
        val resultMap = mutableMapOf<String, Set<String>>()
        val studentAttributes = student.attributes!!
        for (group in groupAttributes) {
            if (group.attributes == null) continue
            val attributes = group.attributes
                .filter {
                    studentAttributes.contains(it) || it.dependency != null && it.dependency.students!!.contains(student)
                }
                .associateBy { it.name!! }
                .keys
            resultMap[group.name!!] = attributes
        }
        return resultMap.toMap()
    }
}