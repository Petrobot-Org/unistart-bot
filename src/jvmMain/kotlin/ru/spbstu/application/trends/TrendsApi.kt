package ru.spbstu.application.trends

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import trends.IdeaResponse
import kotlin.random.Random

fun Routing.trendsApi() {
    authenticate {
        route("/trends") {
            post("/idea") {
                call.respond(IdeaResponse(Random.nextInt()))
            }
        }
    }
}
