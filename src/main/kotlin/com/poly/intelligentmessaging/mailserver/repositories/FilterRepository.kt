package com.poly.intelligentmessaging.mailserver.repositories

import com.poly.intelligentmessaging.mailserver.domain.models.FilterModel
import com.poly.intelligentmessaging.mailserver.domain.projections.FilterProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FilterRepository : JpaRepository<FilterModel, UUID> {

    @Modifying
    @Query(
        """
            select cast(f.id as varchar)           as id,
                   f.name                          as filterName,
                   e.email,
                   f.expression,
                   f.mode,
                   f.created,
                   cast(stf.id_student as varchar) as idStudent
            from filter f
                     inner join student_to_filter stf on f.id = stf.id_filter
                     inner join email e on f.id_email_send = e.id
            where cast(id_staff as varchar) = ?1
        """,
        nativeQuery = true
    )
    fun getFilters(idStaff: String): MutableList<FilterProjection>
}