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
import java.security.MessageDigest

/**
 * Manage security
 */
fun Application.configureSecurity() {

    fun hash(algo: String, seed: String): ByteArray {
        val sha = MessageDigest.getInstance(algo)
        return sha.digest(seed.toByteArray())
    }

    fun generateHex(seed: String): String {
        val b = hash("SHA-512", seed)
        val hexDigits : List<Char> = ('a'..'f') + ('0'..'9')
        val buffer = StringBuffer()
        for (j in b.indices) {
            buffer.append(hexDigits[b[j].toInt() shr 4 and 0x0f])
            buffer.append(hexDigits[b[j].toInt() and 0x0f])
        }
        return buffer.toString()
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
        val sessionFile = File(".sessions")
        sessionFile.mkdirs()

        val secretEncryptKey = hex(generateHex(CONFIG.webCfg.secretEncryptKey))
        val secretSignKey = hex(generateHex(CONFIG.webCfg.secretSignKey))

        cookie<User.SessionUser>("fpi_session", directorySessionStorage(sessionFile)) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 24 * 30 // 30 days
            cookie.extensions["SameSite"] = "lax"
            cookie.secure = true
            cookie.httpOnly = true
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
}
