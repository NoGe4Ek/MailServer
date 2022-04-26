package com.poly.intelligentmessaging.mailserver.configuration.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtUser(
    private val username: String,
    private val password: String,
    private val roles: Set<String>
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val listGrantedAuthorities = mutableSetOf<GrantedAuthority>()
        for (role in roles) listGrantedAuthorities.add(SimpleGrantedAuthority(role))
        return listGrantedAuthorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}