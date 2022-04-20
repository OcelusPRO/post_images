package yt.ftnl.plugins

import io.ktor.server.plugins.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.response.*
import io.ktor.server.request.*

/**
 * Configure base HTTP server
 */
fun Application.configureHTTP() {
    install(HttpsRedirect) {
            sslPort = 443
            permanentRedirect = true
    }

    install(HSTS) {
        includeSubDomains = true
    }

    install(DefaultHeaders) {
        header("Made-By", "OcelusPRO")
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (86400) ))
                ContentType.Text.JavaScript -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (86400) ))
                ContentType.Text.Html -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (86400) ))
                ContentType.Image.Any -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (3600) ))
                ContentType.Video.Any -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (7200) ))
                else -> null
            }
        }
    }

}
