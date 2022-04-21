package yt.ftnl.plugins

import io.ktor.server.sessions.*
import io.ktor.server.application.*
import io.ktor.util.*
import yt.ftnl.CONFIG
import java.util.*

/**
 * Manage security
 */
fun Application.configureSecurity() {
    fun hash(str: String): Long {
        var h = 654321657L // prime
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

    data class UserLoginSession(val count: Int = 0)
    install(Sessions) {
        val secretEncryptKey = hex(generateHex(CONFIG.webCfg.secretEncryptKey))
        val secretSignKey = hex(generateHex(CONFIG.webCfg.secretSignKey))

        cookie<UserLoginSession>("fpi_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 24 * 30 // 30 days
            cookie.extensions["SameSite"] = "lax"
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
}
