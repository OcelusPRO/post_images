package yt.ftnl.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

/**
 * Configuration class.
 *
 * @property dbConfig Database configuration.
 * @property webCfg Web configuration.
 */
data class Configuration(
    val dbConfig: DatabaseConfig = DatabaseConfig(),
    val webCfg: WebConfig = WebConfig()
) {
    /**
     * Exception if error occurs while loading configuration file.
     *
     * @param message The error message.
     */
    class ConfigurationException(message: String) : Exception(message)


    /**
     * Database configuration.
     *
     * @property host Database host.
     * @property port Database port.
     * @property database Database name.
     * @property user Database username.
     * @property password Database password.
     * @property prefix Database prefix.
     */
    data class DatabaseConfig(
        val prefix: String = "",
        val host: String = "",
        val port: Int = 3306,
        val user: String = "",
        val password: String = "",
        val database: String = "",
    )

    /**
     * Web configuration.
     * @property port Web port.
     * @property host Web host.
     * @property secretSignKey Secret sign key.
     * @property secretEncryptKey Secret encrypt key.
     * @property sslConfig Ssl configuration
     * @property serverAddress Server address.
     */
    data class WebConfig(
        val port: Int = 8080,
        val host: String = "0.0.0.0",
        val secretSignKey: String = "",
        val secretEncryptKey: String = "",
        val sslConfig: SslConfig = SslConfig(),
        val serverAddress: String = "https://localhost:${sslConfig.securePort}",
    )

    /**
     * Ssl configuration
     * @property securePort ssl port
     * @property keystorePath path to keystore
     * @property keyAlias key name
     * @property keyPassword key password
     * @property keyStorePassword keyStore password
     */
    data class SslConfig(
        val securePort: Int = 8443,
        val keystorePath: String = "./keystore.jks",
        val keyAlias: String = "keyAlias",
        val keyPassword: String = "foobar",
        val keyStorePassword: String = "foobar"
    )

    companion object {
        /**
         * Loads the configuration from the given file.
         * @param file The file to load the configuration from.
         * @return The loaded configuration.
         * @throws ConfigurationException If the file is not a valid configuration file.
         */
        fun loadConfiguration(file: File): Configuration {
            if (file.createNewFile()) {
                val config = Configuration()
                file.writeText(GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(config))
                throw ConfigurationException("Veuillez remplir le fichier de configuration")
            }
            return try {
                val cfg = Gson().fromJson(file.readText(), Configuration::class.java)
                file.writeText(GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(cfg))
                cfg
            } catch (e: Exception) {
                throw ConfigurationException("La configuration n'est pas valide")
            }
        }
    }
}

