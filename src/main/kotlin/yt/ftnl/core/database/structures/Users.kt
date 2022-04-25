package yt.ftnl.core.database.structures

import io.ktor.server.auth.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import yt.ftnl.CONFIG
import yt.ftnl.core.database.structures.Users.id
import yt.ftnl.core.database.structures.Users.password
import yt.ftnl.core.database.structures.Users.rLevel
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
 * @property rLevel The user's rank level.
 * @property service user service state
 * @property srv_state The user's service state.
 */
object Users : Table("${CONFIG.dbConfig.prefix}staff") {
    val id: Column<Int> = integer("id").autoIncrement()
    val user_id: Column<String> = varchar("staff_id", 22)
    val username: Column<String?> = varchar("pseudo", 255).nullable().default(null)
    val password: Column<String> = text("password")
    val rank: Column<String> = text("rank_name")
    val rLevel: Column<Int> = integer("staff_perm")
    val service: Column<String> = text("service")
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
 * @property rLevel The user's rank level.
 * @property service user service state
 * @property srv_state The user's service state.
 */
data class User(
    val id: Int,
    val user_id: String,
    var username: String?,
    var password: String,
    var rank: String,
    var rLevel: Int,
    var service: String,
    var srv_state: Boolean
) {

    /**
     * @author Ocelus
     * @since 1.0.0
     *
     * This class is used to store the settings of a user session.
     *
     * @property id The database unique ID.
     * @property user_id The user's ID.
     * @property username The user's name.
     * @property rank The user's rank.
     * @property rLevel The user's rank level.
     * @property service user service state
     * @property srv_state The user's service state.
     * @property lastRefresh The last data refresh
     */
    data class SessionUser(
        val id: Int,
        val user_id: String,
        var username: String?,
        var rank: String,
        var rLevel: Int,
        var service: String,
        var srv_state: Boolean,
        var lastRefresh: Long = System.currentTimeMillis()
    ): Principal {

        /**
         * Update last data refresh
         */
        fun updateLastRefresh() {
            val u = getByDid(user_id)
            if (u != null){
                lastRefresh = System.currentTimeMillis()
                username = u.username
                rank = u.rank
                rLevel = u.rLevel
                service = u.service
                srv_state = u.srv_state
            }
        }
    }

    /**
     * Convert User to SessionUser
     */
    fun toSessionUser() = SessionUser(id, user_id, username, rank, rLevel, service, srv_state)
    companion object {

        /**
         * Get user settings
         * @param uid User id
         * @return [User]
         */
        fun getByUid(uid: Int): User? {
            return transaction {
                Users.select { Users.id eq uid }.map {
                    fromRaw(it)
                }.firstOrNull()
            }
        }

        /**
         * Get user settings
         * @param did Discord User id
         * @return [User]
         */
        fun getByDid(did: String?): User? {
            return transaction {
                if (did == null) return@transaction null
                Users.select { user_id eq did }.map {
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
                raw[rLevel],
                raw[service],
                raw[srv_state]
            )
        }
    }
}