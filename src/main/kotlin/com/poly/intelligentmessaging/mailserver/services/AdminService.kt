package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.EmailBox
import com.poly.intelligentmessaging.mailserver.domain.dto.AccessDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.ResponseDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.RoleDTO
import com.poly.intelligentmessaging.mailserver.domain.models.PersonModel
import com.poly.intelligentmessaging.mailserver.domain.models.StaffModel
import com.poly.intelligentmessaging.mailserver.repositories.AccessRepository
import com.poly.intelligentmessaging.mailserver.repositories.PersonRepository
import com.poly.intelligentmessaging.mailserver.repositories.RoleRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.util.generatePassword
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
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
}