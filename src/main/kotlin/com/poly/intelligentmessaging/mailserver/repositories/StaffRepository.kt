package com.poly.intelligentmessaging.mailserver.repositories

import com.poly.intelligentmessaging.mailserver.domain.models.StaffModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StaffRepository : JpaRepository<StaffModel, UUID> {

    @Query(
        value = "select s.id, s.id_person, s.password, s.created " +
                "from staff s inner join person p on s.id_person = p.id " +
                "where p.email like ?1",
        nativeQuery = true
    )
    fun getStaffByEmail(email: String): StaffModel?
}
