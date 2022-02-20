package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.models.custom.MessageFields
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import javax.mail.internet.InternetAddress

@Component
class EmailSender {

    @Autowired
    private val javaMailSender: JavaMailSender? = null

    fun sendEmailSMTP(messageFields: MessageFields) {
        requireNotNull(javaMailSender) { throw ClassNotFoundException("JavaMailSender was not created") }
        val mimeMessage = javaMailSender.createMimeMessage()
        val content = messageFields.contentMultipart != null
        val email = MimeMessageHelper(mimeMessage, content, "UTF-8")
        email.setTo(messageFields.recipientAddress!!.toTypedArray())
        if (content) {
            email.mimeMessage.setContent(messageFields.contentMultipart)
        } else {
            email.mimeMessage.setText(messageFields.contentString)
        }
        email.setSubject(messageFields.subject)
        email.setFrom(InternetAddress(messageFields.senderAddress))
        javaMailSender.send(mimeMessage)
    }
}