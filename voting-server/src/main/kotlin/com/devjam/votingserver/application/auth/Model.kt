package com.devjam.votingserver.application.auth

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

data class AuthCommand(
    val username: String,
    val password: String
)

sealed class AuthResult

data class SuccessfulAuth(
    val username: String,
    val token: String
) : AuthResult()

class UsernameAlreadyExists(username: String) : AuthResult() {
    val message = "Username $username is already in use"
}

class InvalidCredentials : AuthResult() {
    val message = "Provided credentials are invalid"
}


@Entity
data class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val username: String = "",
    val password: String = "",
)