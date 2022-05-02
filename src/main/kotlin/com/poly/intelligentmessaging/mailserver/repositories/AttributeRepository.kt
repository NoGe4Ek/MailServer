package com.poly.intelligentmessaging.mailserver.repositories

import com.poly.intelligentmessaging.mailserver.domain.models.AttributeModel
import com.poly.intelligentmessaging.mailserver.domain.projections.AttributeProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AttributeRepository : JpaRepository<AttributeModel, UUID> {

    @Modifying
    @Query(
        """
            select
                cast(a.id as varchar) as id,
                a.name as attributeName,
                ga.name as groupName,
                a.expression,
                a.created,
                cast(s.id as varchar) as studentId
            from attribute a
                     inner join group_attributes ga on a.id_group_attribute = ga.id
                     inner join student_to_attribute sta on a.id = sta.id_attribute
                     inner join student s on sta.id_student = s.id
            where cast(ga.id_staff as varchar) = ?2
            or cast(ga.id_staff as varchar) = ?1
        """,
        nativeQuery = true
    )
    fun getAttributes(idStaff: String, basicId: String): MutableList<AttributeProjection>

    @Modifying
    @Query(
        """
            select
                cast(a.id as varchar) as id,
                a.name as attributeName,
                ga.name as groupName,
                a.expression,
                a.created,
                cast(s.id as varchar) as studentId
            from attribute a
                    inner join group_attributes ga on a.id_group_attribute = ga.id
                    inner join student_to_attribute sta on a.id = sta.id_attribute
                    inner join student s on sta.id_student = s.id
            where cast(ga.id_staff as varchar) = ?1
        """,
        nativeQuery = true
    )
    fun getAttributesCurrentStaff(idStaff: String): MutableList<AttributeProjection>


    fun findAllByStaffId(idStaff: UUID): Set<AttributeModel>

    fun findAllByStaffIdOrStaffId(idStaff: UUID, basicId: UUID): Set<AttributeModel>
}
