package com.devjam.votingserver.infrastructure.security

import com.devjam.votingserver.application.auth.UserEntity
import com.devjam.votingserver.application.auth.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.context.SecurityContextPersistenceFilter
import org.springframework.stereotype.Component

@Configuration
class SecurityConfig(private val jwtFilter: JwtFilter) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.formLogin().disable()
        http.addFilterAfter(jwtFilter, SecurityContextPersistenceFilter::class.java)
        http.exceptionHandling().authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        http.authorizeRequests()
            .antMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}

@Component
class AuthenticationProvider(private val userRepository: UserRepository) {
    fun getPrincipal(): UserEntity {
        val principal = SecurityContextHolder.getContext().authentication?.principal as String? ?: throw UnauthorizedAccessException()
        return userRepository.findByUsername(principal) ?: throw UnauthorizedAccessException()
    }
}

class UnauthorizedAccessException: RuntimeException() {
    override val message = "Logged user was not found"
}
