package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeIdDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeNameDTO
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.domain.projections.GroupNameProjection
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupAttributesService {

    @Autowired
    val groupAttributesRepository: GroupAttributesRepository? = null

    @Autowired
    val staffRepository: StaffRepository? = null

    fun getGroupAttributes(idStaff: String): MutableSet<GroupAttributeDTO> {
        val result = mutableSetOf<GroupAttributeDTO>()
        val setGroups = groupAttributesRepository!!.findAllByStaffIdOrStaffId(
            UUID.fromString(idStaff),
            UUID.fromString(BASIC_ID_STAFF)
        )
        for (group in setGroups) {
            if (group.attributes!!.isEmpty()) continue
            val dto = GroupAttributeDTO(
                group.id.toString(),
                group.name!!,
                group.attributes.associateBy { attribute -> attribute.name!! }.keys
            )
            result.add(dto)
        }
        return result
    }

    fun getGroupNames(idStaff: String): MutableList<GroupNameProjection> {
        return groupAttributesRepository!!.getGroupNames(idStaff, BASIC_ID_STAFF)
    }

    fun getGroupNamesCurrentStaff(idStaff: String): MutableList<GroupNameProjection> {
        return groupAttributesRepository!!.getGroupNamesCurrentStaff(idStaff)
    }

    fun createGroupName(groupAttributeNameDTO: GroupAttributeNameDTO): GroupAttributeNameDTO {
        val staff = staffRepository!!.findById(UUID.fromString(groupAttributeNameDTO.idStaff)).get()
        groupAttributesRepository!!.save(GroupAttributesModel(name = groupAttributeNameDTO.groupName, staff = staff))
        return groupAttributeNameDTO
    }

    fun deleteGroupAttribute(groupAttributeIdDTO: GroupAttributeIdDTO): GroupAttributeIdDTO {
        groupAttributesRepository!!.deleteById(UUID.fromString(groupAttributeIdDTO.idGroupAttribute))
        return groupAttributeIdDTO
    }
}