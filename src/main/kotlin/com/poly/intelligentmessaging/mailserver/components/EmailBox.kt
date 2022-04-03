package com.poly.intelligentmessaging.mailserver.components

import com.poly.intelligentmessaging.mailserver.configuration.MailProperties
import com.poly.intelligentmessaging.mailserver.util.EmailAuthenticator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.mail.Folder
import javax.mail.Session

@Component
class EmailBox {

    @Autowired
    val mailProperties: MailProperties? = null

    fun getNumberOfMails(auth: EmailAuthenticator): Int {
        val inbox = getInbox(auth) ?: return 0
        inbox.open(Folder.READ_ONLY)
        return inbox.messageCount
    }

    private fun getInbox(auth: EmailAuthenticator): Folder? {
        val properties = mailProperties!!.getPOP3Properties()
        val session = Session.getDefaultInstance(properties, auth)
        val store = session.store
        store.connect(properties.getProperty(mailProperties!!.hostPOP3), auth.login, auth.password)
        return store.getFolder(mailProperties!!.folder)
    }
}