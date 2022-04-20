package yt.ftnl.plugins

import io.ktor.serialization.gson.*
import io.ktor.server.plugins.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*


/**
 * Configure the serialization of the application.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
            disableHtmlEscaping()
        }
    }
}
