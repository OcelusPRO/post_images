package yt.ftnl.plugins

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.mustache.Mustache
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.io.File

/**
 * Configure template folder
 */
fun Application.configureTemplating() {
    install(Mustache) {
        val templateFolder = File("templates")
        templateFolder.mkdirs()
        mustacheFactory = DefaultMustacheFactory(File("./templates"))
    }
}
