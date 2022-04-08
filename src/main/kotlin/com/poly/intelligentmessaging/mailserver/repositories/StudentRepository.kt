package com.poly.intelligentmessaging.mailserver.repositories

import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.domain.projections.StudentProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StudentRepository : JpaRepository<StudentModel, UUID> {

    @Modifying
    @Query(
        value = """
            select cast(s.id as varchar) as id,
                   concat(p.last_name, ' ', p.first_name, ' ', p.patronymic) as name,
                   p.email,
                   ga.name as groupAttributeName,
                   a.name as attributeName
            from student s
            inner join person p on s.id_person = p.id
            inner join student_to_attribute sta on s.id = sta.id_student
            inner join attribute a on a.id = sta.id_attribute
            inner join group_attributes ga on a.id_group_attribute = ga.id
            where cast(ga.id_staff as varchar) = 'ad7a8951-2f95-4619-802b-1285c3279623' 
            or cast(ga.id_staff as varchar) = ?1
        """,
        nativeQuery = true
    )
    fun findAllStudents(idStaff: String): MutableList<StudentProjection>
}