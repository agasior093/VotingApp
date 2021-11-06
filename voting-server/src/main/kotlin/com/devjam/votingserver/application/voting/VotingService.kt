package com.devjam.votingserver.application.voting

import com.devjam.votingserver.application.auth.User
import com.devjam.votingserver.infrastructure.security.AuthenticationProvider
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class VotingService(
    private val pollRepository: PollRepository,
    private val authenticationProvider: AuthenticationProvider
) {

    @Transactional
    fun vote(command: VoteCommand): VoteResult {
        val user = authenticationProvider.getPrincipal()
        return pollRepository.findById(command.pollId).map { doVote(it, command, user) }.orElseGet { PollNotFound() }
    }

    private fun doVote(poll: Poll, command: VoteCommand, user: User): VoteResult {
        if (poll.answers.flatMap { it.voters }.contains(user)) return UserAlreadyVoted()
        return if (!poll.answers.map { it.id }.containsAll(command.answerIds)) AnswerNotFound()
        else {
            command.answerIds.map { id -> poll.answers.find { it.id == id } }.forEach {
                it?.voters?.add(user)
            }
            pollRepository.save(poll)
            SuccessfulVote
        }
    }

}
