package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.domain.dto.ShareDTO
import com.poly.intelligentmessaging.mailserver.domain.models.AttributeModel
import com.poly.intelligentmessaging.mailserver.domain.models.FilterModel
import com.poly.intelligentmessaging.mailserver.domain.models.NotificationModel
import com.poly.intelligentmessaging.mailserver.repositories.AttributeRepository
import com.poly.intelligentmessaging.mailserver.repositories.FilterRepository
import com.poly.intelligentmessaging.mailserver.repositories.NotificationRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class NotificationService {

    @Autowired
    private val staffRepository: StaffRepository? = null

    @Autowired
    private val notificationRepository: NotificationRepository? = null

    @Autowired
    private val filterRepository: FilterRepository? = null

    @Autowired
    private val attributeRepository: AttributeRepository? = null

    fun createNotifications(shareDTO: ShareDTO, isFilter: Boolean): ShareDTO {
        val producer = staffRepository!!.findById(UUID.fromString(shareDTO.idCurrentStaff)).get()
        for (staffId in shareDTO.staffIds!!) {
            val consumer = staffRepository.findById(UUID.fromString(staffId)).get()
            var filter: FilterModel? = null
            var attribute: AttributeModel? = null
            if (isFilter) {
                filter = filterRepository!!.findById(UUID.fromString(shareDTO.id!!)).get()
            } else {
                attribute = attributeRepository!!.findById(UUID.fromString(shareDTO.id!!)).get()
            }
            val notification = NotificationModel(
                consumer = consumer,
                producer = producer,
                filter = filter,
                attribute = attribute
            )
            notificationRepository!!.save(notification)
        }
        return shareDTO
    }
}
