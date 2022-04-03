package com.poly.intelligentmessaging.mailserver.domain

import javax.mail.internet.MimeMultipart

data class MessageFields(
    val senderAddress: String,
    var recipientAddress: Set<String>?,
    val subject: String,
    var contentMultipart: MimeMultipart? = null,
    var contentString: String? = null
) {
    override fun toString(): String {
        return "MessageFields(senderAddress='$senderAddress', " +
                "recipientAddress=$recipientAddress, " +
                "subject='$subject', " +
                "contentMultipart=$contentMultipart, " +
                "contentString=$contentString)"
    }
}
