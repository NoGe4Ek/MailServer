package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.EmailBox
import com.poly.intelligentmessaging.mailserver.components.ExcelHandler
import com.poly.intelligentmessaging.mailserver.domain.StudentExcel
import com.poly.intelligentmessaging.mailserver.domain.dto.AccessDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.ResponseDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.RoleDTO
import com.poly.intelligentmessaging.mailserver.domain.models.*
import com.poly.intelligentmessaging.mailserver.repositories.*
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import com.poly.intelligentmessaging.mailserver.util.generatePassword
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Service
class AdminService {

    @Autowired
    private val roleRepository: RoleRepository? = null

    @Autowired
    private val accessRepository: AccessRepository? = null

    @Autowired
    private val staffRepository: StaffRepository? = null

    @Autowired
    private val bCryptPasswordEncoder: BCryptPasswordEncoder? = null

    @Autowired
    private val personRepository: PersonRepository? = null

    @Autowired
    private val emailBox: EmailBox? = null

    @Autowired
    private val excelHandler: ExcelHandler? = null

    @Autowired
    private val studentRepository: StudentRepository? = null

    @Autowired
    private val attributeRepository: AttributeRepository? = null

    @Autowired
    private val groupAttributesRepository: GroupAttributesRepository? = null

    fun setup(responseDTO: ResponseDTO): ResponseDTO {
        val access = accessRepository!!.findById(UUID.fromString(responseDTO.idRequest)).get()
        val role = roleRepository!!.findByName(responseDTO.role!!)
        val newPassword = generatePassword()
        val staff = StaffModel(
            person = personRepository!!.save(
                PersonModel(
                    lastName = access.lastName,
                    firstName = access.firstName,
                    patronymic = access.patronymic,
                    email = access.email
                )
            ),
            password = bCryptPasswordEncoder!!.encode(newPassword),
            roles = setOf(role)
        )
        staffRepository!!.save(staff)
        emailBox!!.sendNoReply(newPassword, access.email!!, true)
        accessRepository.deleteById(access.id!!)
        return responseDTO
    }

    fun change(responseDTO: ResponseDTO): ResponseDTO {
        val access = accessRepository!!.findById(UUID.fromString(responseDTO.idRequest)).get()
        val role = roleRepository!!.findByName(responseDTO.role!!)
        val staff = access.staff!!
        val rolesStaff = staff.roles!!.toMutableSet()
        rolesStaff.add(role)
        staff.roles = rolesStaff
        staffRepository!!.save(staff)
        accessRepository.deleteById(access.id!!)
        return responseDTO
    }

    fun reject(responseDTO: ResponseDTO): ResponseDTO {
        accessRepository!!.deleteById(UUID.fromString(responseDTO.idRequest))
        return responseDTO
    }

    fun getAccessList(): Set<AccessDTO> {
        val accessRequests = accessRepository!!.findAll()
        val listAccessDTO = mutableListOf<AccessDTO>()
        for (request in accessRequests) {
            val staff = request.staff
            val staffId = staff?.id
            var role: String? = null
            if (staff != null) staff.roles!!.forEach { role = it.name!! }
            val fullName = "${request.lastName} ${request.firstName} ${request.patronymic}"
            val accessDTO = AccessDTO(
                idRequest = request.id.toString(),
                idStaff = if (request.staff != null) staffId.toString() else null,
                fullName = fullName,
                email = request.email!!,
                department = request.department!!,
                highSchool = request.highSchool!!,
                role = role,
                date = request.created!!.toLocalDate().toString()
            )
            listAccessDTO.add(accessDTO)
        }
        return listAccessDTO.toSet()
    }

    fun getRoles(): Set<RoleDTO> {
        val roles = roleRepository!!.findAll()
        val rolesDTO = mutableSetOf<RoleDTO>()
        roles.forEach { role -> rolesDTO.add(RoleDTO(role.id.toString(), role.name!!, role.level!!)) }
        return rolesDTO
    }

    fun updateDB(file: MultipartFile): Map<String, String> {
        val (students, groups) = excelHandler!!.parseExcel(file)
        val attributes = attributeRepository!!.findAllByStaffId(UUID.fromString(BASIC_ID_STAFF))
        if (attributes.isEmpty()) createDataBase(groups, students)
        else updateDataBase(attributes, students)
        return mapOf("status" to "success")
    }

    private fun updateDataBase(attributes: Set<AttributeModel>, students: MutableSet<StudentExcel>) {
        val studentModels = studentRepository!!.findAll()
        for (attribute in attributes) {
            val filteredStudents = students.filter {
                it.attributes[attribute.group!!.name] != null && it.attributes[attribute.group!!.name] == attribute.name
            }
            if (filteredStudents.isEmpty()) {
                if (attribute.group!!.attributes!!.size == 1) groupAttributesRepository!!.delete(attribute.group!!)
                else attributeRepository!!.delete(attribute)
                continue
            }
            val studentsForAttribute = mutableSetOf<StudentModel>()
            for (student in filteredStudents) {
                var studentModel = studentModels.find { it.person!!.email == student.email }
                if (studentModel == null) {
                    val person = personRepository!!.save(
                        PersonModel(
                            lastName = student.lastName,
                            firstName = student.firstName,
                            patronymic = student.patronymic,
                            email = student.email,
                        )
                    )
                    studentModel = studentRepository.save(StudentModel(person = person))
                } else studentModels.remove(studentModel)
                studentsForAttribute.add(studentModel!!)
            }
            attribute.students = studentsForAttribute
            attribute.created = LocalDateTime.now()
            attributeRepository!!.save(attribute)
        }
        studentRepository.deleteAll(studentModels)
    }

    private fun createDataBase(groups: Map<String, Set<String>>, students: Set<StudentExcel>) {
        val staff = staffRepository!!.findById(UUID.fromString(BASIC_ID_STAFF)).get()
        for (group in groups) {
            val newGroup = groupAttributesRepository!!.save(GroupAttributesModel(staff = staff, name = group.key))
            for (attribute in group.value) {
                val studentsForAttribute = mutableSetOf<StudentModel>()
                val studentModels = studentRepository!!.findAll()
                val filteredStudents = students.filter {
                    it.attributes[group.key] != null && it.attributes[group.key] == attribute
                }
                for (student in filteredStudents) {
                    val finder = studentModels.find { it.person!!.email == student.email }
                    if (finder != null) {
                        studentsForAttribute.add(finder)
                        continue
                    }
                    val person = personRepository!!.save(
                        PersonModel(
                            lastName = student.lastName,
                            firstName = student.firstName,
                            patronymic = student.patronymic,
                            email = student.email,
                        )
                    )
                    val newStudent = studentRepository.save(StudentModel(person = person))
                    studentsForAttribute.add(newStudent)
                }
                val newAttribute = AttributeModel(
                    staff = staff,
                    dependency = null,
                    group = newGroup,
                    name = attribute,
                    expression = null,
                    link = false,
                    students = studentsForAttribute
                )
                attributeRepository!!.save(newAttribute)
            }
        }
    }
}