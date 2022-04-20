package yt.ftnl.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

/**
 * Configuration class.
 *
 * @property dbConfig Database configuration.
 */
data class Configuration(
    val dbConfig: DatabaseConfig = DatabaseConfig(),
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

