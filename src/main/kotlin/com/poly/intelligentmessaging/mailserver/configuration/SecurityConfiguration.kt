package com.poly.intelligentmessaging.mailserver.configuration

import com.poly.intelligentmessaging.mailserver.configuration.jwt.JwtTokenFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    private val jwtTokenFilter: JwtTokenFilter? = null

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }

    override fun configure(http: HttpSecurity) {
        http
            .httpBasic().disable()
            .csrf().disable()

        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http
            .authorizeRequests()
            .antMatchers("/attributes/deleteGroupAttribute").hasAuthority("USER")
            .antMatchers("/attributes**").hasAuthority("USER")
            .antMatchers("/attributes/**").hasAuthority("USER")
            .antMatchers("/staff**").hasAuthority("USER")
            .antMatchers("/staff/**").hasAuthority("USER")
            .antMatchers("/filters**").hasAuthority("USER")
            .antMatchers("/filters/**").hasAuthority("USER")
            .antMatchers("/profile**").hasAuthority("USER")
            .antMatchers("/profile/**").hasAuthority("USER")
            .antMatchers("/lists**").hasAuthority("USER")
            .antMatchers("/lists/**").hasAuthority("USER")
            .antMatchers("/login**").permitAll()
            .antMatchers("/login/**").permitAll()
            .anyRequest().authenticated()

        http
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
