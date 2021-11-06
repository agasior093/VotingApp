package com.devjam.votingserver.application.auth


import com.devjam.votingserver.infrastructure.security.JwtTokenProvider
import org.springframework.data.repository.CrudRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface UserRepository : CrudRepository<UserEntity, Long> {
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): UserEntity?
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
        val userEntity =
            userRepository.save(UserEntity(username = command.username, password = passwordEncoder.encode(command.password)))
        return successfulAuth(userEntity)
    }

    fun login(command: AuthCommand) =
        userRepository.findByUsername(command.username)?.let { validateCredentials(it, command) }
            ?: InvalidCredentials()

    private fun validateCredentials(userEntity: UserEntity, command: AuthCommand) =
        if (passwordEncoder.matches(command.password, userEntity.password)) successfulAuth(userEntity) else InvalidCredentials()

    private fun successfulAuth(userEntity: UserEntity) = SuccessfulAuth(username = userEntity.username, tokenProvider.generateToken(userEntity.username))
}