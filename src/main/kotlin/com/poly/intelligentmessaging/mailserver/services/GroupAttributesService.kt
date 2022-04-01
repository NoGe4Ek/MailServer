package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.domain.dto.GroupAttributeDTO
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupAttributesService {

    @Autowired
    val groupAttributesRepository: GroupAttributesRepository? = null

    fun getGroupAttributes(): MutableList<GroupAttributeDTO> {
        val listResult = mutableListOf<GroupAttributeDTO>()
        val listGroups = groupAttributesRepository!!.getGroupAttributes()
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

    fun getGroupNames(): List<String> {
        val groups = groupAttributesRepository!!.findAll()
        val listGroupNames = mutableListOf<String>()
        groups.forEach { listGroupNames.add(it.name!!) }
        return listGroupNames.toList()
    }

    fun createGroupName(name: String): GroupAttributesModel {
        return groupAttributesRepository!!.save(GroupAttributesModel(name = name))
    }
}