package com.poly.intelligentmessaging.mailserver.configuration.jwt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenFilter : OncePerRequestFilter() {

    @Autowired
    private val jwtTokenProvider: JwtTokenProvider? = null

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtTokenProvider!!.resolveToken(request)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            if (authentication != null) {
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }
}