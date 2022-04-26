package yt.ftnl.core.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import yt.ftnl.core.Configuration
import yt.ftnl.core.database.structures.Users

/**
 * Initializes the database.
 *
 * @param conf [Configuration.DatabaseConfig] The database configuration
 */
class DBManager(private val conf: Configuration.DatabaseConfig) {

    init {
        Database.connect(
            url = "jdbc:mysql://${conf.host}:${conf.port}/${conf.database}?useSSL=false",
            //driver = "com.mysql.cj.jdbc.Driver",
            user = conf.user,
            password = conf.password,
        )

        transaction {
            // Init tables, create missing columns
            SchemaUtils.createMissingTablesAndColumns(
                Users
            )
        }
    }

}