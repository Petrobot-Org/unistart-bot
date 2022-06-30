package ru.spbstu.application.configuration

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ru.spbstu.application.trendyfriendy.trendyFriendyApi

fun Application.configureRouting() {
    routing {
        static("/static") {
            resources()
        }
        trendyFriendyApi()
    }
}
