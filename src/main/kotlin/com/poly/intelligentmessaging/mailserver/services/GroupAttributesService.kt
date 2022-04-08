package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeIdDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeNameDTO
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.domain.projections.GroupNameProjection
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupAttributesService {

    @Autowired
    val groupAttributesRepository: GroupAttributesRepository? = null

    @Autowired
    val staffRepository: StaffRepository? = null

    fun getGroupAttributes(idStaff: String): MutableList<GroupAttributeDTO> {
        val listResult = mutableListOf<GroupAttributeDTO>()
        val listGroups = groupAttributesRepository!!.getGroupAttributes(idStaff)
        listGroups.forEach { group ->
            val temp = GroupAttributeDTO(
                group.getId(),
                group.getGroupName(),
                group.getAttributes().split("|").filter { it != "" }
            )
            listResult.add(temp)
        }
        return listResult
    }

    fun getGroupNames(idStaff: String): MutableList<GroupNameProjection> {
        return groupAttributesRepository!!.getGroupNames(idStaff)
    }

    fun getGroupNamesCurrentStaff(idStaff: String): MutableList<GroupNameProjection> {
        return groupAttributesRepository!!.getGroupNamesCurrentStaff(idStaff)
    }

    fun createGroupName(groupAttributeNameDTO: GroupAttributeNameDTO, idStaff: String): GroupAttributeNameDTO {
        val staff = staffRepository!!.findById(UUID.fromString(idStaff)).get()
        groupAttributesRepository!!.save(GroupAttributesModel(name = groupAttributeNameDTO.groupName, staff = staff))
        return groupAttributeNameDTO
    }

    fun deleteGroupAttribute(groupAttributeIdDTO: GroupAttributeIdDTO): GroupAttributeIdDTO {
        groupAttributesRepository!!.deleteById(UUID.fromString(groupAttributeIdDTO.idGroupAttribute))
        return groupAttributeIdDTO
    }
}