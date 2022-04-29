package yt.ftnl.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.plugins.httpsredirect.*
import yt.ftnl.CONFIG

/**
 * Configure base HTTP server
 */
fun Application.configureHTTP() {
    install(HSTS) {
        includeSubDomains = true
        maxAgeInSeconds = 10
    }
*/
    install(DefaultHeaders) {
        header("Made-By", "OcelusPRO")
        header(HttpHeaders.Server, "Kotlin Server")
    }

    install(Compression) {
        gzip {
            matchContentType(ContentType.Text.Any)
            priority = 0.9
            minimumSize(1024)
            condition { request.headers[HttpHeaders.Referrer]?.startsWith(CONFIG.webCfg.serverAddress) == true }
        }
        deflate {
            matchContentType(ContentType.Text.Any)
            priority = 1.0
            minimumSize(1024)
            condition { request.headers[HttpHeaders.Referrer]?.startsWith(CONFIG.webCfg.serverAddress) == true }
        }
    }
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.JavaScript -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (86400) ))
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (86400) ))
                ContentType.Text.Html -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (86400) ))
                ContentType.Image.Any -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (3600) ))
                ContentType.Video.Any -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (7200) ))
                else -> null
            }
        }
    }

}
