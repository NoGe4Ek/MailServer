package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.EmailBox
import com.poly.intelligentmessaging.mailserver.configuration.jwt.JwtTokenProvider
import com.poly.intelligentmessaging.mailserver.domain.dto.AuthRequestDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.AuthResponseDTO
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class AuthService {

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val jwtTokenProvider: JwtTokenProvider? = null

    @Autowired
    private val staffRepository: StaffRepository? = null

    @Autowired
    private val bCryptPasswordEncoder: BCryptPasswordEncoder? = null

    @Autowired
    private val emailBox: EmailBox? = null

    fun check(authRequestDTO: AuthRequestDTO): AuthResponseDTO {
        try {
            val auth = authenticationManager!!.authenticate(
                UsernamePasswordAuthenticationToken(authRequestDTO.login, authRequestDTO.password)
            ).principal as UserDetails
            val token = jwtTokenProvider!!.generateToken(auth)
            val username = jwtTokenProvider.getUsername(token)
            val staff = staffRepository!!.getStaffByEmail(username)
            val person = staff!!.person!!
            val fullName = "${person.lastName} ${person.firstName} ${person.patronymic}"
            return AuthResponseDTO(
                status = true,
                idStaff = staff.id.toString(),
                token = token,
                fullName = fullName,
                email = username,
                roles = staff.roles!!.associateBy { it.name!! }.keys
            )
        } catch (e: UsernameNotFoundException) {
            return AuthResponseDTO(false)
        } catch (e: BadCredentialsException) {
            return AuthResponseDTO(false)
        } catch (e: ExpiredJwtException) {
            return AuthResponseDTO(false)
        }
    }

    fun reset(authRequestDTO: AuthRequestDTO): AuthRequestDTO {
        val staff = staffRepository!!.getStaffByEmail(authRequestDTO.login!!) ?: return authRequestDTO
        val person = staff.person!!
        val newPassword = generatePassword()
        staff.password = bCryptPasswordEncoder!!.encode(newPassword)
        staffRepository.save(staff)
        emailBox!!.sendNoReply(newPassword, person.email!!)
        return authRequestDTO
    }

    private fun generatePassword(): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*?."
        val password = StringBuilder()
        for (i in 0 until 12) {
            val isUpper = Random.nextInt(2)
            val symbol = alphabet[Random.nextInt(0, alphabet.length)]
            password.append(if (isUpper == 0 && symbol.isLetter()) symbol.lowercase() else symbol)
        }
        return password.toString()
    }
}