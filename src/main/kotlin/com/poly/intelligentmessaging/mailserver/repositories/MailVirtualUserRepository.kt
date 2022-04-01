package com.poly.intelligentmessaging.mailserver.repositories

import com.poly.intelligentmessaging.mailserver.domain.models.MailVirtualUserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MailVirtualUserRepository : JpaRepository<MailVirtualUserModel, UUID> {

    @Query(
        value = "SELECT * FROM mail_virtual_user WHERE id_filter = ?1 AND id_staff = ?2",
        nativeQuery = true
    )
    fun findVirtualUserByFilterIdAndStaffId(idFilter: UUID, idStaff: UUID): MailVirtualUserModel
}