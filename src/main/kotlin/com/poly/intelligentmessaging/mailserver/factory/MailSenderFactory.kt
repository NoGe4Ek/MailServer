package com.poly.intelligentmessaging.mailserver.factory

import org.springframework.mail.javamail.JavaMailSender

interface MailSenderFactory {
    fun getSender(email: String, password: String): JavaMailSender
}