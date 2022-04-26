package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.configuration.jwt.JwtTokenProvider
import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class StaffService {

    @Autowired
    private val staffRepository: StaffRepository? = null

    @Autowired
    private val bCryptPasswordEncoder: BCryptPasswordEncoder? = null

    @Autowired
    private val jwtTokenProvider: JwtTokenProvider? = null

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    fun getStaff(staffDTO: StaffDTO): MutableList<StaffDTO> {
        val listStaffDTO = mutableListOf<StaffDTO>()
        val listStaffModel = staffRepository!!.findAll()
        listStaffModel.forEach { staff ->
            if (staff.id.toString() != staffDTO.id && staff.id.toString() != BASIC_ID_STAFF) {
                val fullName = "${staff.person!!.lastName} ${staff.person.firstName} ${staff.person.patronymic}"
                listStaffDTO.add(StaffDTO(staff.id.toString(), fullName))
            }
        }
        return listStaffDTO
    }

    fun changePassword(newPasswordDTO: NewPasswordDTO): NewTokenDTO {
        val staff = staffRepository!!.findById(UUID.fromString(newPasswordDTO.idStaff)).get()
        return if (bCryptPasswordEncoder!!.matches(newPasswordDTO.oldPassword, staff.password)) {
            staff.password = bCryptPasswordEncoder.encode(newPasswordDTO.newPassword)
            staffRepository.save(staff)
            val login = staff.person!!.email
            val auth = authenticationManager!!.authenticate(
                UsernamePasswordAuthenticationToken(login, newPasswordDTO.newPassword)
            ).principal as UserDetails
            val token = jwtTokenProvider!!.generateToken(auth)
            NewTokenDTO(status = "success", token = token)
        } else NewTokenDTO(status = "error")
    }

    fun changeAccess(staffDTO: StaffDTO) {
        TODO()
    }

    fun getNotifications(staffDTO: StaffDTO): Set<NotificationDTO> {
        TODO()
    }

    fun acceptRequest(nrDTO: NotificationResponseDTO): Set<NotificationDTO> {
        TODO()
    }

    fun rejectRequest(nrDTO: NotificationResponseDTO): Set<NotificationDTO> {
        TODO()
    }
}
