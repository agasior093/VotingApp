package com.devjam.votingserver.infrastructure.web

import com.devjam.votingserver.application.auth.AuthCommand
import com.devjam.votingserver.application.auth.AuthService
import com.devjam.votingserver.application.auth.SuccessfulAuth
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthApi(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody command: AuthCommand) =
        when (val authResult = authService.register(command)) {
            is SuccessfulAuth -> ResponseEntity(authResult, HttpStatus.OK)
            else -> ResponseEntity(authResult, HttpStatus.UNAUTHORIZED)
        }

    @PostMapping("/login")
    fun login(@RequestBody command: AuthCommand) =
        when (val authResult = authService.login(command)) {
            is SuccessfulAuth -> ResponseEntity(authResult, HttpStatus.OK)
            else -> ResponseEntity(authResult, HttpStatus.UNAUTHORIZED)
        }
}
