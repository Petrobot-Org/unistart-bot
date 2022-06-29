package ru.spbstu.application.trendyfriendy

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject
import trendyfriendy.Idea
import trendyfriendy.IdeaResponse
import kotlin.random.Random

fun Routing.trendyFriendyApi() {
    val service: TrendyFriendyService by inject()
    authenticate {
        route("/trendy-friendy") {
            post("/idea") {
                val idea = call.receive<Idea>()
                call.respond(IdeaResponse(service.addIdea(userId(), idea)))
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.userId(): Long {
    return call.principal<UserIdPrincipal>()!!.name.toLong()
}
