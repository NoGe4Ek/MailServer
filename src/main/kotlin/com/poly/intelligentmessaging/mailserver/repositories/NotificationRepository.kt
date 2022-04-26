package com.poly.intelligentmessaging.mailserver.repositories

import com.poly.intelligentmessaging.mailserver.domain.models.NotificationModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationRepository : JpaRepository<NotificationModel, UUID> {
    fun findByConsumerId(consumerId: UUID): Set<NotificationModel>
}