package yt.ftnl.plugins

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*


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
