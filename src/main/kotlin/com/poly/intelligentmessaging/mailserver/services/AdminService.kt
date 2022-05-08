package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.EmailBox
import com.poly.intelligentmessaging.mailserver.components.ExcelHandler
import com.poly.intelligentmessaging.mailserver.domain.StudentExcel
import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.models.*
import com.poly.intelligentmessaging.mailserver.repositories.*
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import com.poly.intelligentmessaging.mailserver.util.generatePassword
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Service
class AdminService {

    private val logger = LoggerFactory.getLogger(AdminService::class.java)

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
        return try {
            val (students, groups) = excelHandler!!.parseExcel(file)
            updateDataBase(groups, students)
            mapOf("status" to "success")
        } catch (e: Exception) {
            mapOf("status" to "error")
        }
    }

    private fun updateDataBase(groups: Map<String, Set<String>>, students: Set<StudentExcel>) {
        val staff = staffRepository!!.findById(UUID.fromString(BASIC_ID_STAFF)).get()
        val validAttributes = mutableSetOf<AttributeModel>()
        val validStudents = mutableSetOf<StudentModel>()
        val validGroup = mutableSetOf<GroupAttributesModel>()
        for (group in groups) {
            var newGroup: GroupAttributesModel? =
                groupAttributesRepository!!.findByNameAndStaffId(group.key, UUID.fromString(BASIC_ID_STAFF))
            if (newGroup == null) {
                newGroup = groupAttributesRepository.save(GroupAttributesModel(staff = staff, name = group.key))
            }
            for (attribute in group.value) {
                val studentAttributes = mutableSetOf<StudentModel>()
                val studentModels = studentRepository!!.findAll()
                val filteredStudents = students.filter {
                    it.attributes[group.key] != null && it.attributes[group.key] == attribute
                }
                for (student in filteredStudents) {
                    val finder = studentModels.find { it.person!!.email == student.email }
                    if (finder != null) {
                        studentAttributes.add(finder)
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
                    studentAttributes.add(newStudent)
                }
                var newAttribute = attributeRepository!!.findByNameAndGroup(attribute, newGroup!!)
                if (newAttribute == null) {
                    newAttribute = AttributeModel(
                        staff = staff,
                        dependency = null,
                        group = newGroup,
                        name = attribute,
                        expression = null,
                        link = false,
                    )
                }
                newAttribute.students = studentAttributes
                newAttribute.created = LocalDateTime.now()
                validAttributes.add(attributeRepository.save(newAttribute))
                validStudents.addAll(studentAttributes)
                validGroup.add(newGroup)
            }
        }
        val allBasic = attributeRepository!!.findAllByStaffId(UUID.fromString(BASIC_ID_STAFF))
        attributeRepository.deleteAll(allBasic - validAttributes)
        val allStudents = studentRepository!!.findAll()
        studentRepository.deleteAll(allStudents - validStudents)
        val allGroups = groupAttributesRepository!!.findAllByStaffId(UUID.fromString(BASIC_ID_STAFF))
        groupAttributesRepository.deleteAll(allGroups - validGroup)
    }

    fun getUsers(): Set<UserDTO> {
        val staffs = staffRepository!!.findAll()
        val result = mutableSetOf<UserDTO>()
        staffs.forEach { staff ->
            val person = staff.person!!
            if (person.email != "basicadmin@poly-sender.ru") {
                val roles = staff.roles!!
                val admin = roles.find { it.level!! == 2 } != null
                val user = roles.find { it.level!! == 1 } != null
                val userDTO = UserDTO(
                    id = staff.id.toString(),
                    lastName = person.lastName!!,
                    firstName = person.firstName!!,
                    patronymic = person.patronymic!!,
                    admin = admin,
                    user = user,
                    date = staff.created!!.toLocalDate().toString()
                )
                result.add(userDTO)
            }
        }
        return result
    }

    fun changeRoles(changeStaffDTO: ChangeStaffDTO): ChangeStaffDTO {
        val staff = staffRepository!!.findById(UUID.fromString(changeStaffDTO.id)).get()
        val roles = roleRepository!!.findAll()
        val newRoles = mutableSetOf<RoleModel>()
        staff.created = LocalDateTime.now()
        for (role in changeStaffDTO.roles) {
            newRoles.add(roles.find { it.name!! == role }!!)
        }
        staff.roles = newRoles
        staffRepository.save(staff)
        return changeStaffDTO
    }

    fun deleteUser(changeStaffDTO: ChangeStaffDTO): ChangeStaffDTO {
        val staff = staffRepository!!.findById(UUID.fromString(changeStaffDTO.id)).get()
        val login = staff.person!!.email!!
        staffRepository.delete(staff)
        logger.info("User: $login was removed")
        return changeStaffDTO
    }
}
