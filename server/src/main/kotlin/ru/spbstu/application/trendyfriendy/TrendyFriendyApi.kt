package ru.spbstu.application.trendyfriendy

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject
import trendyfriendy.Idea
import trendyfriendy.IdeaResponse
import trendyfriendy.TrendCardSet
import kotlin.random.Random

fun Routing.trendyFriendyApi() {
    val service: TrendyFriendyService by inject()
    authenticate {
        route("/trendy-friendy") {
            route("/ideas") {
                get {
                    call.respond(IdeaResponse(service.getIdeaCount(userId())))
                }
                post {
                    val idea = call.receive<Idea>()
                    call.respond(IdeaResponse(service.addIdea(userId(), idea)))
                }
                post("/finish") {
                    service.finish(userId())
                    call.respond(HttpStatusCode.OK)
                }
            }
            route("/cards") {
                get {
                    val cards = service.getCards(userId())
                    call.respond(cards)
                }
                post("/{id}") {
                    val cards = service.generateCards(userId(), 0)
                    call.respond(cards)
                }
                route("/sets") {
                    get {
                        call.respond(
                            listOf(
                                TrendCardSet("Технологические", 0),
                                TrendCardSet("Глобальные рыночные", 1),
                                TrendCardSet("Gartner", 2),
                                TrendCardSet("Deloitte", 3),
                                TrendCardSet("IBM Institute", 4),
                                TrendCardSet("Skolkovo", 5)
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.userId(): Long {
    return call.principal<UserIdPrincipal>()!!.name.toLong()
}
