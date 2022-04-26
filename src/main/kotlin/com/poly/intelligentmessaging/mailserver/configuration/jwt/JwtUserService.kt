package com.poly.intelligentmessaging.mailserver.configuration.jwt

import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class JwtUserService : UserDetailsService {

    @Autowired
    private val staffRepository: StaffRepository? = null

    override fun loadUserByUsername(userName: String): UserDetails {
        val staff = staffRepository!!.getStaffByEmail(userName)
        if (staff != null) {
            return JwtUser(
                staff.person!!.email!!,
                staff.password!!,
                staff.roles!!.associateBy { it.name!! }.keys
            )
        } else {
            throw UsernameNotFoundException("User not found: $userName")
        }
    }
}