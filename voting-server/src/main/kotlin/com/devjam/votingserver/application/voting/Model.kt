package com.devjam.votingserver.application.voting

import com.devjam.votingserver.application.auth.User
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.time.LocalDateTime
import javax.persistence.*

data class CreatePollCommand(
    val question: String,
    val answers: List<String>
)

data class VoteCommand(
    val pollId: Long,
    val answerIds: List<Long>
)

sealed class VoteResult
data class UserAlreadyVoted(val message: String = "You already voted in this poll"): VoteResult()
data class PollNotFound(val message: String = "Poll does not exist"): VoteResult()
data class AnswerNotFound(val message: String = "Answer does not exist"): VoteResult()
object SuccessfulVote : VoteResult()

data class PollDto(
    val id: Long,
    val question: String,
    val answers: List<AnswerDto>
)

data class AnswerDto(
    val id: Long,
    val content: String,
    val voters: List<String>
)

@Entity
data class Poll(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val question: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany
    @Cascade(CascadeType.PERSIST)
    val answers: List<Answer> = emptyList()
)

@Entity
data class Answer(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val content: String = "",

    @ManyToMany
    val voters: MutableList<User> = mutableListOf()
)
