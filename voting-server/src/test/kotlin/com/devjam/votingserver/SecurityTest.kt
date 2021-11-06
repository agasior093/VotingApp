package com.devjam.votingserver


import com.devjam.votingserver.application.voting.CreatePollCommand
import com.devjam.votingserver.application.voting.VoteCommand
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val mapper = jacksonObjectMapper()

    @Test
    fun shouldNotAllowUnauthenticatedAccessToCreatePoll() {
        mockMvc.perform(
            post("/poll")
                .content(mapper.writeValueAsString(CreatePollCommand(question = "", answers = emptyList())))
                .contentType("application/json"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun shouldNotAllowUnauthenticatedAccessToVoting() {
        mockMvc.perform(
            post("/poll/vote")
                .content(mapper.writeValueAsString(VoteCommand(pollId = 1, answerIds = emptyList())))
                .contentType("application/json"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun shouldNotAllowUnauthenticatedAccessToGetPolls() {
        mockMvc.perform(
            get("/poll/")).andExpect(status().isUnauthorized)
    }

}
