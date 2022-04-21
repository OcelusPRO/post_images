package yt.ftnl.core.database.structures

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import yt.ftnl.CONFIG
import yt.ftnl.core.database.structures.Users.id
import yt.ftnl.core.database.structures.Users.password
import yt.ftnl.core.database.structures.Users.rank
import yt.ftnl.core.database.structures.Users.service
import yt.ftnl.core.database.structures.Users.srv_state
import yt.ftnl.core.database.structures.Users.user_id
import yt.ftnl.core.database.structures.Users.username

/**
 * Represents a user's  settings.
 * @property id The database unique ID.
 * @property user_id The user's ID.
 * @property username The user's name.
 * @property password The user's password.
 * @property rank The user's rank.
 * @property service user service state
 * @property srv_state The user's service state.
 */
object Users : Table("${CONFIG.dbConfig.prefix}staff") {
    val id: Column<Int> = integer("id").autoIncrement()
    val user_id: Column<String> = varchar("staff_id", 22)
    val username: Column<String?> = varchar("pseudo", 2).nullable().default(null)
    val password: Column<String> = text("password")
    val rank: Column<String> = text("rank_name")
    val service: Column<String> = text("service").default("N/A")
    val srv_state: Column<Boolean> = bool("srv_state").default(false)

    override val primaryKey: PrimaryKey = PrimaryKey(this.id, name = "id")
}

/**
 * @author Ocelus
 * @since 1.0.0
 *
 * This class is used to store the settings of a user.
 *
 * @property id The database unique ID.
 * @property user_id The user's ID.
 * @property username The user's name.
 * @property password The user's password.
 * @property rank The user's rank.
 * @property service user service state
 * @property srv_state The user's service state.
 */
data class User(
    val id: Int,
    val user_id: String,
    var username: String?,
    var password: String,
    var rank: String,
    var service: String,
    var srv_state: Boolean
) {

    companion object {

        /**
         * Get user lang settings
         * @param uid User id
         * @return UserLangSettingsData
         */
        fun get(uid: Int): User? {
            return transaction {
                Users.select { Users.id eq uid }.map {
                    fromRaw(it)
                }.firstOrNull()
            }
        }

        /**
         * Transform ResultRow to UserLangSettingsData
         * @param raw ResultRow
         * @return UserLangSettingsData
         */
        private fun fromRaw(raw: ResultRow): User {
            return User(
                raw[id],
                raw[user_id],
                raw[username],
                raw[password],
                raw[rank],
                raw[service],
                raw[srv_state]
            )
        }
    }
}