package com.devjam.votingserver.application.voting

import com.devjam.votingserver.application.auth.UserEntity
import com.devjam.votingserver.infrastructure.security.AuthenticationProvider
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface PollRepository : PagingAndSortingRepository<PollEntity, Long>

@Service
class PollService(
    private val pollRepository: PollRepository,
    private val authenticationProvider: AuthenticationProvider
) {

    fun createPoll(command: CreatePollCommand): Poll {
        val pollEntity = pollRepository.save(PollEntity(
            question = command.question,
            answerEntities = command.answers.map { AnswerEntity(content = it) }
        ))
        return PollWithoutResults(
            id = pollEntity.id,
            question = pollEntity.question,
            answers = pollEntity.answerEntities.map { answerWithoutResults(it) }
        )
    }

    fun getAllPolls(): List<Poll> {
        val user = authenticationProvider.getPrincipal()
        return pollRepository.findAll(Sort.by("createdAt").descending()).map { toPoll(it, user) }
    }

    private fun toPoll(pollEntity: PollEntity, userEntity: UserEntity): Poll {
        val userAlreadyVoted = pollEntity.answerEntities.flatMap { it.voters }.contains(userEntity)
        return if (userAlreadyVoted) PollWithResults(
            id = pollEntity.id,
            question = pollEntity.question,
            answers = pollEntity.answerEntities.map {
                answerWithResults(it)
            }
        ) else PollWithoutResults(
            id = pollEntity.id,
            question = pollEntity.question,
            answers = pollEntity.answerEntities.map {
                answerWithoutResults(it)
            }
        )
    }

    private fun answerWithResults(answer: AnswerEntity) =
        AnswerWithResults(id = answer.id, content = answer.content, voters = answer.voters.map { it.username })

    private fun answerWithoutResults(answer: AnswerEntity) =
        AnswerWithoutResults(id = answer.id, content = answer.content)
}


