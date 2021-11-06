package com.devjam.votingserver.infrastructure.web

import com.devjam.votingserver.application.voting.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/poll")
class PollApi(private val pollService: PollService, private val voteService: VoteService) {

    @PostMapping
    fun createPoll(@RequestBody command: CreatePollCommand) =
        ResponseEntity(pollService.createPoll(command), HttpStatus.CREATED)

    @PostMapping("/vote")
    fun vote(@RequestBody command: VoteCommand): ResponseEntity<VoteResult> {
        return when (val result = voteService.vote(command)) {
            SuccessfulVote -> ResponseEntity(result, HttpStatus.OK);
            else -> ResponseEntity(result, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping
    fun getPolls() = ResponseEntity(pollService.getAllPolls(), HttpStatus.OK)
}

