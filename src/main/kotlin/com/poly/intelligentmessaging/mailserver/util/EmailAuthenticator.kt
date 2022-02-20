package com.poly.intelligentmessaging.mailserver.util

import javax.mail.Authenticator
import javax.mail.PasswordAuthentication

class EmailAuthenticator(val login: String, val password: String) : Authenticator() {
    public override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(login, password)
    }
}
