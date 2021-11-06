package com.devjam.votingserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VotingServerApplication

fun main(args: Array<String>) {
	runApplication<VotingServerApplication>(*args)
}
