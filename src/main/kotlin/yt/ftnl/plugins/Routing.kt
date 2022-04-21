package yt.ftnl.plugins

import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import java.io.File

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
            val staticFile = File("./static")
            staticFile.mkdirs()
            staticRootFolder = staticFile
        }
    }
}