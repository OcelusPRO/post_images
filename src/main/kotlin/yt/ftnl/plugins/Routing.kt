package yt.ftnl.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.mustache.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import yt.ftnl.core.database.structures.User
import yt.ftnl.core.hash
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manage routes
 */
fun Application.configureRouting() {
    install(AutoHeadResponse)

    routing {
        fun getImageData(imgName: String, session: User.SessionUser?): Map<String, Any?> {
            val regex = Regex("^(\\d+)_(\\d+)\\.(\\w+)$")
            regex.matchEntire(imgName) ?: return mapOf("error" to "Invalid image name")

            val args = imgName.split("_")
            val staffId = args[0].toIntOrNull() ?: 0
            val fileData = args[1].split(".")
            val date = fileData[0].toLongOrNull() ?: 0
            val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
            val netDate = Date(date)
            val ext = fileData[1]
            val staff = User.getByUid(staffId)

            var managable = false
            if (session != null) {
                if(staff?.user_id == session.user_id) managable = true
                else if (session.rLevel > (staff?.rLevel ?: 0) && session.rLevel >= 3) managable = true
            }

            return mapOf(
                "iname" to imgName,
                "NAME" to staff?.username,
                "ID" to staff?.user_id,
                "GRADE" to staff?.rank,
                "STATE" to staff?.service,
                "DATE" to sdf.format(netDate),
                "TYPE" to ext,
                "isManagable" to managable
            )
        }

        get("/") {
            call.respond(MustacheContent("index.hbs", mapOf("" to "")))
        }

        static("/static") {
            val staticFile = File("./static")
            staticFile.mkdirs()
            staticRootFolder = staticFile
        }
    }
}