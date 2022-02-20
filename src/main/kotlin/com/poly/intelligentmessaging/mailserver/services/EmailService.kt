package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.EmailForwarder
import com.poly.intelligentmessaging.mailserver.components.EmailSender
import com.poly.intelligentmessaging.mailserver.exceptions.MailException
import com.poly.intelligentmessaging.mailserver.repositories.FilterRepository
import com.poly.intelligentmessaging.mailserver.repositories.MailVirtualUserRepository
import com.poly.intelligentmessaging.mailserver.util.EMAIL_FORWARDER_EXCEPTION_MESSAGE
import com.poly.intelligentmessaging.mailserver.util.EMAIL_SENDER_EXCEPTION_MESSAGE
import com.poly.intelligentmessaging.mailserver.util.EmailAuthenticator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class EmailService {

    @Autowired
    private val emailSender: EmailSender? = null

    @Autowired
    private val emailForwarder: EmailForwarder? = null

//    @Autowired
//    private val forwardRepository: ForwardRepository? = null

    @Autowired
    private val mailVirtualUserRepository: MailVirtualUserRepository? = null

    @Autowired
    private val filterRepository: FilterRepository? = null

    fun sendEmail(filter: UUID, sender: UUID) {
        requireNotNull(emailForwarder) { throw ClassNotFoundException(EMAIL_FORWARDER_EXCEPTION_MESSAGE) }
        requireNotNull(emailSender) { throw ClassNotFoundException(EMAIL_SENDER_EXCEPTION_MESSAGE) }
        requireNotNull(mailVirtualUserRepository) {
            throw ClassNotFoundException("MailVirtualUserRepository was not found")
        }
        requireNotNull(filterRepository)

        val mailVirtualUserModel = mailVirtualUserRepository.findVirtualUserByFilterIdAndStaffId(filter, sender)

        val senderAddress = mailVirtualUserModel.staff!!.person!!.email!!
        val emailAuth = EmailAuthenticator(mailVirtualUserModel.email!!, mailVirtualUserModel.password!!)
        val listStudents = filterRepository.findById(filter).get().student!!

        val messageFields = emailForwarder.getMessageForStudents(emailAuth, senderAddress, listStudents)
        requireNotNull(messageFields) { throw MailException("No new emails from $sender") }
        messageFields.forEach { emailSender.sendEmailSMTP(it) }
    }

    fun forwardEmail(filter: UUID, recipient: UUID) {
        requireNotNull(emailForwarder) { throw ClassNotFoundException(EMAIL_FORWARDER_EXCEPTION_MESSAGE) }
        requireNotNull(emailSender) { throw ClassNotFoundException(EMAIL_SENDER_EXCEPTION_MESSAGE) }
        requireNotNull(mailVirtualUserRepository) {
            throw ClassNotFoundException("MailVirtualUserRepository was not found")
        }

        val mailVirtualUserModel = mailVirtualUserRepository.findVirtualUserByFilterIdAndStaffId(filter, recipient)
        val recipientAddress = mailVirtualUserModel.staff!!.person!!.email!!
        val emailAuth = EmailAuthenticator(mailVirtualUserModel.email!!, mailVirtualUserModel.password!!)

        val listMessagesForForward = emailForwarder.getMessageForForward(emailAuth, listOf(recipientAddress))
        listMessagesForForward.forEach { emailSender.sendEmailSMTP(it) }
    }
}
