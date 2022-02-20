package com.poly.intelligentmessaging.mailserver.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class MailProperties {

    companion object {
        const val STORE_PROTOCOL = "mail.store.protocol"
        const val POP3_HOST = "mail.pop3.host"
        const val POP3_PORT = "mail.pop3.port"
    }

    @Value("\${$STORE_PROTOCOL}")
    val storeProtocol: String? = null

    @Value("\${$POP3_HOST}")
    val hostPOP3: String? = null

    @Value("\${$POP3_PORT}")
    val portPOP3: Int? = null

    @Value("\${mail.pop3.folder}")
    val folder: String? = null

    fun getPOP3Properties(): Properties {
        val properties = Properties()
        properties[STORE_PROTOCOL] = storeProtocol
        properties[POP3_HOST] = hostPOP3
        properties[POP3_PORT] = portPOP3
        return properties
    }
}
