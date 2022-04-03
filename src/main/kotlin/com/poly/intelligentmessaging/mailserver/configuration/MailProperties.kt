package com.poly.intelligentmessaging.mailserver.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*


@Component
class MailProperties {

    companion object {
        //pop3 labels
        const val STORE_PROTOCOL = "mail.store.protocol"
        const val POP3_HOST = "mail.pop3.host"
        const val POP3_PORT = "mail.pop3.port"
        const val FOLDER = "mail.pop3.folder"

        //smtp labels
        const val SMTP_HOST = "mail.host"
        const val SMTP_PORT = "mail.port"
        const val TRANSPORT = "mail.transport.protocol"
        const val START_TLS_EN = "mail.smtp.starttls.enable"
        const val START_TLS_REQ = "mail.smtp.starttls.required"
        const val SMTP_AUTH = "mail.smtp.auth"
        const val ENCODING = "mail.default-encoding"
        const val TEST_CONNECTION = "mail.test-connection"
    }

    @Value("\${$STORE_PROTOCOL}")
    private val storeProtocol: String? = null

    @Value("\${$POP3_HOST}")
    val hostPOP3: String? = null

    @Value("\${$POP3_PORT}")
    val portPOP3: Int? = null

    @Value("\${$FOLDER}")
    val folder: String? = null

    @Value("\${$SMTP_HOST}")
    val hostSMTP: String? = null

    @Value("\${$SMTP_PORT}")
    val portSMTP: Int? = null

    @Value("\${$TRANSPORT}")
    private val transport: String? = null

    @Value("\${$START_TLS_EN}")
    private val startTLSEn: Boolean? = null

    @Value("\${${START_TLS_REQ}}")
    private val startTLSReq: Boolean? = null

    @Value("\${$SMTP_AUTH}")
    private val authSMTP: Boolean? = null

    @Value("\${$ENCODING}")
    private val defaultEncoding: String? = null

    @Value("\${$TEST_CONNECTION}")
    private val testConnection: Boolean? = null


    fun getPOP3Properties(): Properties {
        val properties = Properties()
        properties[STORE_PROTOCOL] = storeProtocol
        properties[POP3_HOST] = hostPOP3
        properties[POP3_PORT] = portPOP3
        return properties
    }

    fun getSMTPProperties(): Properties {
        val properties = Properties()
        properties[SMTP_HOST] = hostSMTP
        properties[SMTP_PORT] = portSMTP
        properties[TRANSPORT] = transport
        properties[START_TLS_EN] = startTLSEn
        properties[START_TLS_REQ] = startTLSReq
        properties[SMTP_AUTH] = authSMTP
        properties[ENCODING] = defaultEncoding
        properties[TEST_CONNECTION] = testConnection
        return properties
    }
}
