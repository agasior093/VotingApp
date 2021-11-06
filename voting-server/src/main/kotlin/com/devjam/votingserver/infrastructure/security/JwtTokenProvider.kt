package com.devjam.votingserver.infrastructure.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider {
    private val tokenSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512)
    private val expirationTime = 864000000

    fun generateToken(username: String): String {
        val now = Date()
        val expirationDate = Date(Date().time + expirationTime)
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expirationDate)
            .signWith(tokenSecret, SignatureAlgorithm.HS512)
            .compact()
    }

    fun getUsername(jwt: String): String {
        val claims = Jwts.parser()
            .setSigningKey(tokenSecret)
            .parseClaimsJws(jwt)
            .body
        return claims.subject
    }

    fun validate(jwt: String): Boolean {
        return try {
            Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(jwt)
            true
        } catch (ex: JwtException) {
            false
        }
    }
}