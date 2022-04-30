package yt.ftnl

import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import yt.ftnl.core.Configuration
import yt.ftnl.core.database.DBManager
import yt.ftnl.plugins.*
import java.io.File
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.*

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

    val keyStoreFile = File(CONFIG.webCfg.sslConfig.keystorePath)
    var keyStore = if (keyStoreFile.exists()) {
        val store = KeyStore.getInstance(KeyStore.getDefaultType())!!
        store.load(keyStoreFile.inputStream(), CONFIG.webCfg.sslConfig.keyStorePassword.toCharArray())
        val cert = store.getCertificate(CONFIG.webCfg.sslConfig.keyAlias) as? X509Certificate
        if (cert?.notAfter?.after(Date()) == false) null else store
    } else null

    keyStore = keyStore ?: generateCertificate(
        file = keyStoreFile,
        keyAlias = CONFIG.webCfg.sslConfig.keyAlias,
        keyPassword = CONFIG.webCfg.sslConfig.keyPassword,
        jksPassword = CONFIG.webCfg.sslConfig.keyStorePassword
    )

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = CONFIG.webCfg.port
            host = CONFIG.webCfg.host
        }

        sslConnector(
            keyStore = keyStore,
            keyAlias = CONFIG.webCfg.sslConfig.keyAlias,
            keyStorePassword = { CONFIG.webCfg.sslConfig.keyStorePassword.toCharArray() },
            privateKeyPassword = { CONFIG.webCfg.sslConfig.keyPassword.toCharArray() }
        ) {
            port = CONFIG.webCfg.sslConfig.securePort
            keyStorePath = keyStoreFile
        }

        module(Application::configureHTTP)
        module(Application::configureRouting)
        module(Application::configureTemplating)
        module(Application::configureSecurity)
        module(Application::configureSerialization)
    }

    embeddedServer(Netty, environment).start(wait = true)
}