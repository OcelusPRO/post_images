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

    val noSortedFolder = File("./uploads/no-sorted")
    if(noSortedFolder.exists()){
        val regex = Regex("^(\\d+)_(\\d+)\\.(\\w+)$")
        noSortedFolder.listFiles()?.forEach {
            if (it.name.matches(regex)) {
                val name = it.name.split("_")
                var success = false
                try {
                    File("./uploads/${name[0]}").mkdirs()
                    val f =  File("./uploads/${name[0]}/${name[1]}")
                    it.copyTo(f)
                    success = true
                }catch (e: Exception){ println(e.message) }
                if (success) it.delete()
            }
        }
    }


    CONFIG = Configuration.loadConfiguration(File(if (args.isNotEmpty()) args[0] else "./config.json"))
    DBManager(CONFIG.dbConfig)

    embeddedServer(Netty, port = CONFIG.webCfg.port, host = CONFIG.webCfg.host) {
        configureRouting()
        configureTemplating()
        configureSecurity()
        configureSerialization()
        configureHTTP()
    }.start(wait = true)
}