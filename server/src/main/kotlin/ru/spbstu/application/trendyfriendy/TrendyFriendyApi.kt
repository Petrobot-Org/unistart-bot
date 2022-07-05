package ru.spbstu.application.trendyfriendy

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.html.*
import org.koin.ktor.ext.inject
import trendyfriendy.Idea
import trendyfriendy.IdeaResponse

fun HTML.index() {
    head {
        meta("viewport", "initial-scale=1, width=device-width")
        title("Trendy Friendy")
        styleLink("/static/styles.css")
    }
    body {
        div {
            id = "root"
        }
        script(src = "https://telegram.org/js/telegram-web-app.js") {}
        script(src = "/static/trendy-friendy-frontend.js") {}
    }
}

fun Routing.trendyFriendyApi() {
    val service: TrendyFriendyService by inject()
    get("/trendy-friendy") {
        call.respondHtml(HttpStatusCode.OK, HTML::index)
    }
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
                post {
                    val fromSets = call.receive<Set<String>>()
                    val cards = service.generateCards(userId(), fromSets)
                    call.respond(cards)
                }
                get("/sets") {
                    call.respond(service.getSets())
                }
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.userId(): Long {
    return call.principal<UserIdPrincipal>()!!.name.toLong()
}
