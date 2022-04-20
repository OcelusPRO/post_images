package yt.ftnl.plugins

import io.ktor.server.sessions.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

/**
 * Manage security
 */
fun Application.configureSecurity() {
    data class UserLoginSession(val count: Int = 0)
    install(Sessions) {
        cookie<UserLoginSession>("fpi_session") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
}
