package com.devjam.votingserver.infrastructure.web

import com.devjam.votingserver.application.voting.*
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/poll")
@Api(description = "Poll API")
internal class PollApi(private val pollService: PollService, private val voteService: VoteService) {

    @PostMapping
    @ApiOperation(value = "Create new poll")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "Created"),
        ApiResponse(code = 401, message = "Unauthorized")
    ])
    @ResponseStatus(HttpStatus.CREATED)
    fun createPoll(
        @RequestHeader(value = "Authorization", required = true) authorization: String,
        @RequestBody command: CreatePollCommand) =
        ResponseEntity(pollService.createPoll(command), HttpStatus.CREATED)

    @PostMapping("/vote")
    @ApiOperation(value = "Add vote")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Success"),
        ApiResponse(code = 400, message = "Bad Request"),
        ApiResponse(code = 401, message = "Unauthorized")
    ])
    fun vote(@RequestHeader(value = "Authorization", required = true) authorization: String, @RequestBody command: VoteCommand): ResponseEntity<VoteResult> {
        return when (val result = voteService.vote(command)) {
            SuccessfulVote -> ResponseEntity(result, HttpStatus.OK);
            else -> ResponseEntity(result, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping
    @ApiOperation(
        value = "List all polls",
        notes = "Lists all polls sorted by creation date descending. Voting results are included only for polls that user took part in.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Success"),
        ApiResponse(code = 401, message = "Unauthorized")
    ])
    fun listPolls(@RequestHeader(value = "Authorization", required = true) authorization: String) = ResponseEntity(pollService.listPolls(), HttpStatus.OK)
}

