package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.repositories.FilterRepository
import com.poly.intelligentmessaging.mailserver.services.FilterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduledTask {

    @Autowired
    val filterService: FilterService? = null

    @Autowired
    val filterRepository: FilterRepository? = null

    @Scheduled(fixedDelay = 10000)
    fun autoMailSending() {
        val filters = filterRepository!!.findAll()
        for (filter in filters) {
            filterService!!.sendEmails(filter)
        }
    }
}