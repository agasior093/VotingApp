package com.devjam.votingserver.application.voting

import com.devjam.votingserver.application.auth.UserEntity
import com.devjam.votingserver.infrastructure.security.AuthenticationProvider
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class VoteService(
    private val pollRepository: PollRepository,
    private val authenticationProvider: AuthenticationProvider
) {

    @Transactional
    fun vote(command: VoteCommand): VoteResult {
        val user = authenticationProvider.getPrincipal()
        return pollRepository.findById(command.pollId).map { doVote(it, command, user) }.orElseGet { PollNotFound() }
    }

    private fun doVote(pollEntity: PollEntity, command: VoteCommand, userEntity: UserEntity): VoteResult {
        if (pollEntity.answerEntities.flatMap { it.voters }.contains(userEntity)) return UserAlreadyVoted()
        return if (!pollEntity.answerEntities.map { it.id }.containsAll(command.answerIds)) AnswerNotFound()
        else {
            command.answerIds.map { id -> pollEntity.answerEntities.find { it.id == id } }.forEach {
                it?.voters?.add(userEntity)
            }
            pollRepository.save(pollEntity)
            SuccessfulVote
        }
    }

}
