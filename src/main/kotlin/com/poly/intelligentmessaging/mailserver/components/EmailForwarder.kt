package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.configuration.MailProperties
import com.poly.intelligentmessaging.mailserver.domain.MessageFields
import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.exceptions.MailException
import com.poly.intelligentmessaging.mailserver.util.EmailAuthenticator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

@Component
class EmailForwarder {

    @Autowired
    val mailProperties: MailProperties? = null

    fun getMessageForForward(auth: EmailAuthenticator, recipients: List<String>): List<MessageFields> {
        val listMessageForForward = mutableListOf<MessageFields>()
        val inbox = getInbox(auth)
        requireNotNull(inbox) { throw MailException("Unable to connect to INBOX. Login: ${auth.login}") }
        inbox.open(Folder.READ_WRITE)
        for (messageIndex in 1..inbox.messageCount) {
            val message = inbox.getMessage(messageIndex) as MimeMessage
            val messageFields = getMessageFields(message, auth.login, recipients)
            listMessageForForward.add(messageFields)
            message.setFlag(Flags.Flag.DELETED, true)
        }
        inbox.close()
        return listMessageForForward
    }

    fun getMessageForStudents(
        auth: EmailAuthenticator,
        sender: String,
        recipients: Set<StudentModel>
    ): List<MessageFields>? {
        val listMessageForForward = mutableListOf<MessageFields>()
        val inbox = getInbox(auth)
        requireNotNull(inbox) { throw MailException("Unable to connect to INBOX. Login: ${auth.login}") }
        inbox.open(Folder.READ_WRITE)
        val messages = inbox.messages.filter {
            println(it.from[0] as InternetAddress)
            (it.from[0] as InternetAddress).address == sender
        }
        if (messages.isEmpty()) return null
        for (message in messages) {
            val messageFields = getMessageFields(
                message as MimeMessage,
                auth.login,
                recipients.associateBy {
                    it.person!!.email!!
                }.keys.toList()
            )
            listMessageForForward.add(messageFields)
            message.setFlag(Flags.Flag.DELETED, true)
        }
        inbox.close()
        return listMessageForForward
    }

    private fun getMessageFields(message: MimeMessage, sender: String, recipients: List<String>): MessageFields {
        val subject = if (message.subject != null) message.subject else ""
        val messageFields = MessageFields(sender, recipients, subject)
        if (message.content !is MimeMultipart) {
            messageFields.contentString = message.content as String
        } else {
            messageFields.contentMultipart = message.content as MimeMultipart
        }
        return messageFields
    }

    private fun getInbox(auth: EmailAuthenticator): Folder? {
        val properties = mailProperties!!.getPOP3Properties()
        val session = Session.getDefaultInstance(properties, auth)
        val store = session.store
        store.connect(properties.getProperty(mailProperties!!.hostPOP3), auth.login, auth.password)
        return store.getFolder(mailProperties!!.folder)
    }
}
