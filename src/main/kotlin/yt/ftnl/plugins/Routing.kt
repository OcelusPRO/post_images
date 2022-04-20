package yt.ftnl.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.request.*

/**
 * Manage routes
 */
fun Application.configureRouting() {
    install(AutoHeadResponse)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        static("/static") {
            resources("static")
        }
    }
}
