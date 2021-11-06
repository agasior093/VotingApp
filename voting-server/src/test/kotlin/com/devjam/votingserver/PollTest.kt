package com.devjam.votingserver

import com.devjam.votingserver.application.auth.UserEntity
import com.devjam.votingserver.application.auth.UserRepository
import com.devjam.votingserver.application.voting.*
import com.devjam.votingserver.infrastructure.security.JwtTokenProvider
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@AutoConfigureMockMvc
class PollTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var pollRepository: PollRepository

    @Autowired
    private lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val mapper = jacksonObjectMapper()

    private lateinit var token: String

    @BeforeEach
    fun setup() {
        token = generateToken()
    }

    @AfterEach
    fun cleanup() {
        pollRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun shouldCreateNewPoll() {
        val command = CreatePollCommand("test question", listOf("test answer", "another test answer"))

        val poll = createPoll(command)

        assertEquals(command.question, poll.question)
        assertTrue(poll.answers.map { it.content }.containsAll(command.answers))
    }

    @Test
    fun shouldAcceptVote() {
        val poll = createPoll(CreatePollCommand("test question", listOf("test answer", "another test answer")))

        vote(poll.id, listOf(poll.answers[0].id)).andExpect(status().isOk)
    }

    @Test
    fun shouldRejectVoteIfUserAlreadyVoted() {
        val poll = createPoll(CreatePollCommand("test question", listOf("test answer", "another test answer")))

        vote(poll.id, listOf(poll.answers[0].id)).andExpect(status().isOk)

        vote(poll.id, listOf(poll.answers[0].id)).andExpect(status().isBadRequest)
    }

    @Test
    fun shouldRejectVoteIfPollDoesNotExist() {
        vote(1, listOf(1)).andExpect(status().isBadRequest)
    }

    @Test
    fun shouldRejectVoteIfAnswerDoesNotExist() {
        val poll = createPoll(CreatePollCommand("test question", listOf("test answer", "another test answer")))

        vote(poll.id, listOf(Long.MAX_VALUE)).andExpect(status().isBadRequest)
    }

    @Test
    fun shouldListPollsWithoutVotingResults() {
        val command = CreatePollCommand("test question 1", listOf("test answer", "another test answer"))
        createPoll(command)
        createPoll(command)

        val responseBody = mapper.readValue<List<PollWithoutResults>>(getPolls().response.contentAsString)

        assertEquals(2, responseBody.size)
    }

    @Test
    fun shouldListPollsWithVotingResults() {
        val command = CreatePollCommand("test question", listOf("test answer", "another test answer"))
        val poll = createPoll(command)
        createPoll(command)

        vote(poll.id, listOf(poll.answers[0].id)).andExpect(status().isOk)

        val responseBody = mapper.readValue<List<Any>>(getPolls().response.contentAsString)

        for (response in responseBody){
            val map = (response as LinkedHashMap<*, *>)
            if((map["id"] as Int) == poll.id.toInt()) {
                val answers = (map["answers"] as ArrayList<*>).map { it as LinkedHashMap<*, *> }
                answers.forEach { assertNotNull(it["voters"])}
            } else {
                val answers = (map["answers"] as ArrayList<*>).map { it as LinkedHashMap<*, *> }
                answers.forEach { assertNull(it["voters"]) }
            }
        }
    }

    private fun createPoll(command: CreatePollCommand): PollWithoutResults {
        val response = mockMvc.perform(
            post("/poll")
                .content(mapper.writeValueAsString(command))
                .contentType("application/json")
                .header("Authorization", token))
            .andExpect(status().isCreated).andReturn()
        return mapper.readValue(response.response.contentAsString)
    }

    private fun vote(questionId: Long, answerIds: List<Long>): ResultActions {
        return mockMvc.perform(
            post("/poll/vote")
                .content(mapper.writeValueAsString(VoteCommand(questionId, answerIds)))
                .contentType("application/json")
                .header("Authorization", token))
    }

    private fun getPolls(): MvcResult {
        return mockMvc.perform(
            get("/poll")
                .header("Authorization", token))
            .andExpect(status().isOk).andReturn()
    }

    private fun generateToken(): String {
        val user = userRepository.save(UserEntity(username = "test",password =  "test"))
        val token = tokenProvider.generateToken(username = user.username)
        return "Bearer $token"
    }
}