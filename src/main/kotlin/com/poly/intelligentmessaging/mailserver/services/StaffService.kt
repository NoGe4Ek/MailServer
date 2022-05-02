package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.configuration.jwt.JwtTokenProvider
import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.models.AccessModel
import com.poly.intelligentmessaging.mailserver.repositories.AccessRepository
import com.poly.intelligentmessaging.mailserver.repositories.NotificationRepository
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

    @Autowired
    private val notificationRepository: NotificationRepository? = null

    @Autowired
    private val accessRepository: AccessRepository? = null

    @Autowired
    private val attributeService: AttributeService? = null

    @Autowired
    private val filterService: FilterService? = null

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
        val staff = staffRepository!!.findById(UUID.fromString(staffDTO.id!!)).get()
        val person = staff.person!!
        val accessModel = AccessModel(
            staff = staff,
            lastName = person.lastName,
            firstName = person.firstName,
            patronymic = person.patronymic,
            email = person.email,
            department = "ИКНТ",
            highSchool = "ВШИСиСТ",
        )
        accessRepository!!.save(accessModel)
    }

    fun getNotifications(staffDTO: StaffDTO): Set<NotificationDTO> {
        val notifications = notificationRepository!!.findByConsumerId(UUID.fromString(staffDTO.id))
        val result = mutableSetOf<NotificationDTO>()
        notifications.forEach { notification ->
            val producer = notification.producer!!
            val fullName = "${producer.person!!.lastName} ${producer.person.firstName} ${producer.person.patronymic}"
            val nameItem =
                if (notification.filter != null) notification.filter.name!! else notification.attribute!!.name!!
            result.add(
                NotificationDTO(
                    idNotification = notification.id.toString(),
                    idOwner = producer.id.toString(),
                    fullNameOwner = fullName,
                    type = if (notification.filter != null) "filter" else "attribute",
                    name = nameItem,
                    date = notification.created!!.toLocalDate().toString(),
                )
            )
        }
        return result
    }

    fun acceptRequest(nrDTO: NotificationResponseDTO): NotificationResponseDTO {
        val notification = notificationRepository!!.findById(UUID.fromString(nrDTO.idNotification)).get()
        if (notification.filter != null) filterService!!.acceptNotification(notification, nrDTO.type!!)
        else attributeService!!.acceptNotification(notification, nrDTO.type!!)
        notificationRepository.deleteById(UUID.fromString(nrDTO.idNotification))
        return nrDTO
    }

    fun rejectRequest(nrDTO: NotificationResponseDTO): NotificationResponseDTO {
        notificationRepository!!.deleteById(UUID.fromString(nrDTO.idNotification))
        return nrDTO
    }
}
