package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.domain.dto.StaffDTO
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StaffService {

    @Autowired
    val staffRepository: StaffRepository? = null

    fun getStaff(currentStaff: String, basicStaff: String): MutableList<StaffDTO> {
        val listStaffDTO = mutableListOf<StaffDTO>()
        val listStaffModel = staffRepository!!.findAll()
        listStaffModel.forEach { staff ->
            if (staff.id.toString() != currentStaff && staff.id.toString() != basicStaff) {
                val fullName = "${staff.person!!.lastName} ${staff.person.firstName} ${staff.person.patronymic}"
                listStaffDTO.add(StaffDTO(staff.id.toString(), fullName))
            }
        }
        return listStaffDTO
    }
}