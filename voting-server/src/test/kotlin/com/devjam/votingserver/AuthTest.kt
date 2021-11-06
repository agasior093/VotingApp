package com.devjam.votingserver

import com.devjam.votingserver.application.auth.AuthCommand
import com.devjam.votingserver.application.auth.SuccessfulAuth
import com.devjam.votingserver.application.auth.UserRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@AutoConfigureMockMvc
class AuthTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val mapper = jacksonObjectMapper()

    @BeforeEach
    fun cleanup() {
        userRepository.deleteAll()
    }

    @Test
    fun shouldRegisterNewUser() {
        val registerCommand = AuthCommand("test", "test")

        val response = register(registerCommand)
            .andExpect(status().isCreated).andReturn()
        val responseBody = mapper.readValue<SuccessfulAuth>(response.response.contentAsString)

        assertEquals(registerCommand.username, responseBody.username)
        assertNotNull(responseBody.token)
    }

    @Test
    fun shouldNotAllowToUseExistingUsername() {
        val registerCommand = AuthCommand("test", "test")
        register(registerCommand).andExpect(status().isCreated)
        register(registerCommand).andExpect(status().isUnauthorized)
    }


    @Test
    fun shouldLoginUser() {
        register(AuthCommand("test", "test")).andExpect(status().isCreated)

        val loginCommand = AuthCommand("test", "test")
        val response = login(loginCommand).andExpect(status().isOk).andReturn()
        val responseBody = mapper.readValue<SuccessfulAuth>(response.response.contentAsString)

        assertEquals(loginCommand.username, responseBody.username)
        assertNotNull(responseBody.token)
    }

    @Test
    fun shouldRejectLoginAttemptIfUsernameDoesNotExist() {
        register(AuthCommand("test", "test")).andExpect(status().isCreated)
        login(AuthCommand("randomUser", "test")).andExpect(status().isUnauthorized)
    }

    @Test
    fun shouldRejectLoginAttemptIfPasswordDoesNotMatch() {
        register(AuthCommand("test", "test")).andExpect(status().isCreated)
        login(AuthCommand("test", "randomPassword")).andExpect(status().isUnauthorized)
    }

    private fun login(command: AuthCommand): ResultActions {
        return mockMvc.perform(
            post("/auth/login")
                .content(mapper.writeValueAsString(command))
                .contentType("application/json")
        )
    }

    private fun register(command: AuthCommand): ResultActions {
        return mockMvc.perform(
            post("/auth/register")
                .content(mapper.writeValueAsString(command))
                .contentType("application/json")
        )
    }
}
