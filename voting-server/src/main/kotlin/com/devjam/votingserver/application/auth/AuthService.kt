package com.devjam.votingserver.application.auth


import com.devjam.votingserver.infrastructure.security.JwtTokenProvider
import org.springframework.data.repository.CrudRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface UserRepository : CrudRepository<User, Long> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): User?
}

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: JwtTokenProvider
) {

    fun register(command: AuthCommand) = if (userRepository.existsByUsername(command.username))
        UsernameAlreadyExists(command.username) else registerNewUser(command)

    private fun registerNewUser(command: AuthCommand): AuthResult {
        val user =
            userRepository.save(User(username = command.username, password = passwordEncoder.encode(command.password)))
        return successfulAuth(user)
    }

    fun login(command: AuthCommand) =
        userRepository.findByUsername(command.username)?.let { validateCredentials(it, command) }
            ?: InvalidCredentials()

    private fun validateCredentials(user: User, command: AuthCommand) =
        if (passwordEncoder.matches(command.password, user.password)) successfulAuth(user) else InvalidCredentials()

    private fun successfulAuth(user: User) = SuccessfulAuth(username = user.username, tokenProvider.generateToken(user.username))
}