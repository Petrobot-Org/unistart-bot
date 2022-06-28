package ru.spbstu.application

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.html.*
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.spbstu.application.configuration.configureAuthentication
import ru.spbstu.application.configuration.configureRouting
import ru.spbstu.application.configuration.configureSerialization
import ru.spbstu.application.telegram.TelegramBot

fun HTML.index() {
    head {
        meta("viewport", "initial-scale=1, width=device-width")
        title("UniStart")
        styleLink("/static/styles.css")
    }
    body {
        div {
            id = "root"
        }
        script(src = "https://telegram.org/js/telegram-web-app.js") {}
        script(src = "/static/unistart.js") {}
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(Koin) {
            slf4jLogger()
            modules(unistartModule)
        }
        configureSerialization()
        configureAuthentication()
        configureRouting()
        get<TelegramBot>().start()
    }.start(wait = true)
}
