package com.poly.intelligentmessaging.mailserver.repositories

import com.poly.intelligentmessaging.mailserver.domain.models.AttributeModel
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AttributeRepository : JpaRepository<AttributeModel, UUID> {

    fun findAllByStaffId(idStaff: UUID): Set<AttributeModel>

    fun findAllByStaffIdOrStaffId(idStaff: UUID, basicId: UUID): Set<AttributeModel>

    fun findByNameAndGroup(name: String, group: GroupAttributesModel): AttributeModel?
}
