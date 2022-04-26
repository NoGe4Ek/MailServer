package com.poly.intelligentmessaging.mailserver.configuration.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest


@Component
class JwtTokenProvider {

    @Value("\${jwt.token.secret}")
    private val secret: String? = null

    @Value("\${jwt.token.expired}")
    private val expired: Long? = null

    @Autowired
    private val jwtUserService: UserDetailsService? = null

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims: MutableMap<String, String> = mutableMapOf()
        claims["authorities"] = userDetails.authorities.joinToString(separator = ",")
        return createToken(claims, userDetails.username)
    }

    fun createToken(claims: Map<String, String>, subject: String): String {
        val now = Date()
        val validity = Date(now.time + expired!!)
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    fun getAuthentication(token: String): Authentication? {
        val userDetails = jwtUserService!!.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getUsername(token: String): String {
        return Jwts
            .parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7, bearerToken.length)
        } else null
    }

    fun validateToken(token: String): Boolean {
        val claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
        return !claims.body.expiration.before(Date())
    }
}
