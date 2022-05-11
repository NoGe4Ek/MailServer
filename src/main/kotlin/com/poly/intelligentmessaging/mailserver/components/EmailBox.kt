package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.configuration.MailProperties
import com.poly.intelligentmessaging.mailserver.domain.MessageFields
import com.poly.intelligentmessaging.mailserver.util.EmailAuthenticator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import javax.mail.AuthenticationFailedException
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

@Component
class EmailBox {

    @Autowired
    val mailProperties: MailProperties? = null

    @Autowired
    val mailSenderFactoryImpl: MailSenderFactoryImpl? = null

    private val logger = LoggerFactory.getLogger(EmailBox::class.java)

    fun getEmails(auth: EmailAuthenticator, recipients: Set<String>) {
        val folder = getInbox(auth)!!
        val sender = mailSenderFactoryImpl!!.getSender(auth.login, auth.password)
        val listMessagesForForward = getListMails(auth.login, recipients, folder)
        listMessagesForForward.forEach { message -> send(message, sender) }
        folder.close()
    }

    fun getNumberOfMails(auth: EmailAuthenticator): Int {
        try {
            val inbox = getInbox(auth) ?: return 0
            inbox.open(Folder.READ_ONLY)
            val count = inbox.messageCount
            inbox.close()
            return count
        } catch (e: AuthenticationFailedException) {
            logger.error("AuthenticationFailedException - login: [${auth.login}]")
            return 0
        }
    }

    fun sendEmails(auth: EmailAuthenticator, recipients: Set<String>, senderAddress: String) {
        val folder = getInbox(auth)!!
        val sender = mailSenderFactoryImpl!!.getSender(auth.login, auth.password)
        val listMessagesForForward = getListMails(senderAddress, recipients, folder)
        listMessagesForForward.forEach { message -> send(message, sender) }
        folder.close()
    }

    fun sendNoReply(password: String, recipient: String, isNew: Boolean) {
        val noReply = "noreply@poly-sender.ru"
        val sender = mailSenderFactoryImpl!!.getSender(noReply, "fon?gfMKUC%d")
        val mimeMessage = sender.createMimeMessage()
        val email = MimeMessageHelper(mimeMessage)
        email.setTo(recipient)
        email.setText(
            """
    ${if (isNew) """Добро пожаловать в PolySender!""".trimIndent() else ""}
    ВНИМАНИЕ! Никому не сообщайте ваш пароль!
    
    Ваш новый пароль: $password
    
    https://poly-sender.ru
    
    С уважением, PolySender!""".trimIndent()
        )
        email.setSubject(if (isNew) "Добро пожаловать!" else "Изменение пароля")
        email.setFrom(noReply)
        sender.send(mimeMessage)
    }

    private fun getMessageFields(message: MimeMessage, sender: String, recipients: Set<String>): MessageFields {
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

    private fun send(messageFields: MessageFields, javaMailSender: JavaMailSender) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val content = messageFields.contentMultipart != null
        val email = MimeMessageHelper(mimeMessage, content, "UTF-8")
        email.setTo(messageFields.recipientAddress!!.toTypedArray()[0])
        if (messageFields.recipientAddress!!.isNotEmpty()) {
            email.setBcc(
                messageFields.recipientAddress!!
                    .toList().subList(1, messageFields.recipientAddress!!.size)
                    .toTypedArray()
            )
        }
        if (content) {
            email.mimeMessage.setContent(messageFields.contentMultipart)
        } else {
            email.mimeMessage.setText(messageFields.contentString)
        }
        email.setSubject(messageFields.subject)
        email.setFrom(InternetAddress(messageFields.senderAddress))
        javaMailSender.send(mimeMessage)
    }

    private fun getListMails(senderAddress: String, recipients: Set<String>, inbox: Folder): List<MessageFields> {
        val listMessage = mutableListOf<MessageFields>()
        inbox.open(Folder.READ_WRITE)
        for (messageIndex in 1..inbox.messageCount) {
            val message = inbox.getMessage(messageIndex) as MimeMessage

            val messageFields = getMessageFields(message, senderAddress, recipients)
            listMessage.add(messageFields)
            message.setFlag(Flags.Flag.DELETED, true)
        }
        return listMessage
    }
}