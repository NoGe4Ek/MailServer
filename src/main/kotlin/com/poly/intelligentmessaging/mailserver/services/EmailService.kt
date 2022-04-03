package com.poly.intelligentmessaging.mailserver.services

import org.springframework.stereotype.Service


@Service
class EmailService {
//
//    @Autowired
//    val emailBox: EmailBox? = null

//    @Autowired
//    private val emailSender: EmailSender? = null
//
//    @Autowired
//    private val emailForwarder: EmailForwarder? = null

//    @Autowired
//    private val forwardRepository: ForwardRepository? = null
//
//    @Autowired
//    private val mailVirtualUserRepository: MailVirtualUserRepository? = null
//
//    @Autowired
//    private val filterRepository: FilterRepository? = null

//    fun sendEmail(filter: UUID) {
//        requireNotNull(emailForwarder) { throw ClassNotFoundException(EMAIL_FORWARDER_EXCEPTION_MESSAGE) }
//        requireNotNull(emailSender) { throw ClassNotFoundException(EMAIL_SENDER_EXCEPTION_MESSAGE) }
////        requireNotNull(mailVirtualUserRepository) {
////            throw ClassNotFoundException("MailVirtualUserRepository was not found")
////        }
//        requireNotNull(filterRepository)
//
//        val mailVirtualUserModel = mailVirtualUserRepository.findByIdFilter(filter)
//
//        val senderAddress = mailVirtualUserModel.staff!!.person!!.email!!
//        val emailAuth = EmailAuthenticator(mailVirtualUserModel.email!!, mailVirtualUserModel.password!!)
//        val listStudents = filterRepository.findById(filter).get().student!!
//
//        val messageFields = emailForwarder.getMessageForStudents(emailAuth, senderAddress, listStudents)
//        requireNotNull(messageFields) { throw MailException("No new emails from $filter") }
//        messageFields.forEach { emailSender.sendEmailSMTP(it) }
//    }

//    fun forwardEmail(filter: UUID) {
//        requireNotNull(emailForwarder) { throw ClassNotFoundException(EMAIL_FORWARDER_EXCEPTION_MESSAGE) }
//        requireNotNull(emailSender) { throw ClassNotFoundException(EMAIL_SENDER_EXCEPTION_MESSAGE) }
//        requireNotNull(mailVirtualUserRepository) {
//            throw ClassNotFoundException("MailVirtualUserRepository was not found")
//        }
//
//        val mailVirtualUserModel = mailVirtualUserRepository.findByIdFilter(filter)
//        val recipientAddress = mailVirtualUserModel.staff!!.person!!.email!!
//        val emailAuth = EmailAuthenticator(mailVirtualUserModel.email!!, mailVirtualUserModel.password!!)
//
//        val listMessagesForForward = emailForwarder.getMessageForForward(emailAuth, listOf(recipientAddress))
//        listMessagesForForward.forEach { emailSender.sendEmailSMTP(it) }
//    }
//
//    fun getNumberOfMails(idFilter: String): Int {
//
//        val emailAuth = EmailAuthenticator(mailVirtualUser.email!!, mailVirtualUser.password!!)
//        return emailBox!!.getNumberOfMails(emailAuth)
//    }
}
