package com.devjam.votingserver.infrastructure.security

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class JwtFilter(private val tokenProvider: JwtTokenProvider) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        httpRequest.getHeader("Authorization")?.let {
            if (tokenProvider.validate(it)) {
                val username = tokenProvider.getUsername(it)
                SecurityContextHolder.getContext().authentication = JwtAuthentication(username)
            }
        }
        chain.doFilter(request, response)
    }
}

internal class JwtAuthentication(private val username: String) : Authentication {
    override fun getPrincipal() = username
    override fun isAuthenticated() = true
    override fun getName() = username
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun getCredentials() = null
    override fun getDetails() = null
    override fun setAuthenticated(b: Boolean) {}
}