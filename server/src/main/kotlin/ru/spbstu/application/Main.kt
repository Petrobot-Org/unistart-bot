package ru.spbstu.application

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.coroutines.launch
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.spbstu.application.configuration.configureAuthentication
import ru.spbstu.application.configuration.configureRouting
import ru.spbstu.application.configuration.configureSerialization
import ru.spbstu.application.telegram.TelegramBot

suspend fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(Koin) {
            slf4jLogger()
            modules(unistartModule)
        }
        install(Compression)
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respondText(text = "${cause.message ?: cause}", status = HttpStatusCode.InternalServerError)
            }
        }
        configureSerialization()
        configureAuthentication()
        configureRouting()
        launch {
            get<TelegramBot>().start()
        }
    }.start(wait = true)
}
