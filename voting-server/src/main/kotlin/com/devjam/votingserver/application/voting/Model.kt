package com.devjam.votingserver.application.voting

import com.devjam.votingserver.application.auth.UserEntity
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

sealed class Poll

data class PollWithoutResults(
    val id: Long,
    val question: String,
    val answers: List<AnswerWithoutResults>,
    val canUserVote: Boolean  = true
): Poll()

data class PollWithResults(
    val id: Long,
    val question: String,
    val answers: List<AnswerWithResults>,
    val canUserVote: Boolean = false
): Poll()

data class AnswerWithoutResults(
    val id: Long,
    val content: String
)

data class AnswerWithResults(
    val id: Long,
    val content: String,
    val voters: List<String>
)

@Entity
data class PollEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val question: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany
    @Cascade(CascadeType.ALL)
    val answerEntities: List<AnswerEntity> = emptyList()
)

@Entity
data class AnswerEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val content: String = "",

    @ManyToMany
    val voters: MutableList<UserEntity> = mutableListOf()
)
