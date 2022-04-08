package com.poly.intelligentmessaging.mailserver.repositories

import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.domain.projections.GroupAttributeProjection
import com.poly.intelligentmessaging.mailserver.domain.projections.GroupNameProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupAttributesRepository : JpaRepository<GroupAttributesModel, UUID> {

    @Modifying
    @Query(
        """
            select
                cast(ga.id as varchar),
                ga.name as groupName,
                string_agg(a.name, '|') as attributes
            from group_attributes ga
            inner join attribute a on ga.id = a.id_group_attribute
            where cast(ga.id_staff as varchar) = 'ad7a8951-2f95-4619-802b-1285c3279623' 
            or cast(ga.id_staff as varchar) = ?1
            group by ga.name, ga.id;
        """,
        nativeQuery = true
    )
    fun getGroupAttributes(idStaff: String): MutableList<GroupAttributeProjection>


    fun findByName(name: String): GroupAttributesModel


    @Modifying
    @Query(
        """
            select cast(ga.id as varchar) as idGroupName, ga.name as groupName from group_attributes ga 
            where cast(ga.id_staff as varchar) = 'ad7a8951-2f95-4619-802b-1285c3279623' 
            or cast(ga.id_staff as varchar) = ?1
        """,
        nativeQuery = true
    )
    fun getGroupNames(idStaff: String): MutableList<GroupNameProjection>

    @Modifying
    @Query(
        """
            select cast(ga.id as varchar) as idGroupName, ga.name as groupName from group_attributes ga 
            where cast(ga.id_staff as varchar) = ?1
        """,
        nativeQuery = true
    )
    fun getGroupNamesCurrentStaff(idStaff: String): MutableList<GroupNameProjection>
}