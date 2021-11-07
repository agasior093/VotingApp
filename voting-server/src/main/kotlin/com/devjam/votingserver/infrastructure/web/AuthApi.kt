package com.devjam.votingserver.infrastructure.web

import com.devjam.votingserver.application.auth.AuthCommand
import com.devjam.votingserver.application.auth.AuthService
import com.devjam.votingserver.application.auth.SuccessfulAuth
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Api(description = "Authorization API")
internal class AuthApi(private val authService: AuthService) {

    @PostMapping("/register")
    @ApiOperation(
        value = "Register operation",
        notes = "Performs new user registration. On success, returns username and JWT token."
    )
    @ApiResponses(value = [
            ApiResponse(code = 200, message = "Success"),
            ApiResponse(code = 401, message = "Unauthorized")
        ])
    fun register(@RequestBody command: AuthCommand) =
        when (val authResult = authService.register(command)) {
            is SuccessfulAuth -> ResponseEntity(authResult, HttpStatus.CREATED)
            else -> ResponseEntity(authResult, HttpStatus.UNAUTHORIZED)
        }

    @PostMapping("/login")
    @ApiOperation(
        value = "Login operation",
        notes = "Performs user login. On success, returns username and JWT token.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Success"),
        ApiResponse(code = 401, message = "Unauthorized")
    ])
    fun login(@RequestBody command: AuthCommand) =
        when (val authResult = authService.login(command)) {
            is SuccessfulAuth -> ResponseEntity(authResult, HttpStatus.OK)
            else -> ResponseEntity(authResult, HttpStatus.UNAUTHORIZED)
        }
}
