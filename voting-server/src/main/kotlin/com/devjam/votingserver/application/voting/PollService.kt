package com.devjam.votingserver.application.voting

import com.devjam.votingserver.application.auth.User
import com.devjam.votingserver.infrastructure.security.AuthenticationProvider
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface PollRepository : PagingAndSortingRepository<Poll, Long>

@Service
class PollService(
    private val pollRepository: PollRepository,
    private val authenticationProvider: AuthenticationProvider
) {

    fun createPoll(command: CreatePollCommand): PollDto {
        val pollEntity = pollRepository.save(Poll(
            question = command.question,
            answers = command.answers.map { Answer(content = it) }
        ))
        return toDto(pollEntity, authenticationProvider.getPrincipal())
    }

    fun getAllPolls(): List<PollDto> {
        val user = authenticationProvider.getPrincipal()
        return pollRepository.findAll(Sort.by("createdAt").descending()).map { toDto(it, user) }
    }

    private fun toDto(poll: Poll, user: User) = PollDto(
        id = poll.id,
        question = poll.question,
        answers = poll.answers.map {
            AnswerDto(
                id = it.id,
                content = it.content,
                voters = getVoters(poll, it, user)
            )
        }
    )

    private fun getVoters(poll: Poll, answer: Answer, user: User): List<String> {
        return if (poll.answers.flatMap { it.voters }.contains(user))
            answer.voters.map { voter -> voter.username }
        else emptyList()
    }
}


