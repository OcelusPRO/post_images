package yt.ftnl

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import yt.ftnl.core.Configuration
import yt.ftnl.core.database.DBManager
import yt.ftnl.plugins.*
import java.io.File

/**
 * Global variable to store configuration instance
 */
lateinit var CONFIG: Configuration

/**
 * Main function
 * *Mon IDE me tape si je met pas la doc Kappa*
 */
fun main(args: Array<String>) {
    CONFIG = Configuration.loadConfiguration(File(if (args.isNotEmpty()) args[0] else "./config.json"))
    DBManager(CONFIG.dbConfig)

    embeddedServer(Netty, port = CONFIG.webCfg.port, host = CONFIG.webCfg.host) {
        configureRouting()
        configureSerialization()
        configureTemplating()
        configureHTTP()
        configureSecurity()
    }.start(wait = true)
}
