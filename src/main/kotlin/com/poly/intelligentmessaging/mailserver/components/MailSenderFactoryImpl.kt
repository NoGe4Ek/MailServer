package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.configuration.MailProperties
import com.poly.intelligentmessaging.mailserver.factory.MailSenderFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component


@Component
class MailSenderFactoryImpl : MailSenderFactory {

    @Autowired
    val mailProperties: MailProperties? = null

    override fun getSender(email: String, password: String): JavaMailSender {
        val mailSender = JavaMailSenderImpl()

        mailSender.javaMailProperties = mailProperties!!.getSMTPProperties()

        mailSender.host = mailProperties!!.hostSMTP!!
        mailSender.port = mailProperties!!.portSMTP!!

        mailSender.username = email
        mailSender.password = password

        return mailSender
    }
}