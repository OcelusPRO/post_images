package yt.ftnl.plugins

import io.ktor.server.sessions.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.util.*
import yt.ftnl.CONFIG
import yt.ftnl.core.database.structures.User
import yt.ftnl.core.hash
import java.io.File
import java.util.*

/**
 * Manage security
 */
fun Application.configureSecurity() {
    fun hash(str: String): Long {
        var h = 654321657L
        val len: Int = str.length
        for (i in 0 until len) {
            h = 31 * h + str[i].code
        }
        return h
    }
    fun generateHex(seed: String): String{
        val random = Random(hash(seed))
        val charPool : List<Char> = ('a'..'f') + ('0'..'9')
        return (1..32)
            .map { random.nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    install(Authentication) {

        form("auth-form") {
            userParamName = "userid"
            passwordParamName = "password"
            validate { credentials ->
                val user = User.getByDid(credentials.name) ?: return@validate null
                if (user.password == credentials.password.hash("SHA-256")) user.toSessionUser()
                else null
            }

            skipWhen { call -> call.sessions.get<User.SessionUser>() != null }
        }

        basic("auth-basic"){
            realm = "Access to API routes"
            skipWhen { call -> call.sessions.get<User.SessionUser>() != null }
            validate { credentials ->
                val user = User.getByDid(credentials.name) ?: return@validate null
                if (user.password == credentials.password.hash("SHA-256")) user.toSessionUser()
                else null
            }
        }

        session<User.SessionUser>("auth-session") {
            validate { session ->
                fun valid(usr: User.SessionUser): Boolean{
                    return usr.srv_state && (usr.lastRefresh + 1000*60*60*2 /* 2 hours */) > System.currentTimeMillis()
                }
                if (valid(session)) session
                else {
                    session.updateLastRefresh()
                    if (valid(session)) session
                    else null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }


    install(Sessions) {
        val secretEncryptKey = hex(generateHex(CONFIG.webCfg.secretEncryptKey))
        val secretSignKey = hex(generateHex(CONFIG.webCfg.secretSignKey))

        cookie<User.SessionUser>("fpi_session", directorySessionStorage(File(".sessions"))) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 24 * 30 // 30 days
            cookie.extensions["SameSite"] = "lax"
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
}
