package yt.ftnl.plugins

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.mustache.Mustache
import io.ktor.server.application.*
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
