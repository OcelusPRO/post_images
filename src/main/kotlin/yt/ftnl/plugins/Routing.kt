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

        get("/login") {
            call.respond(MustacheContent("login.hbs", mapOf("" to null)))
        }

        authenticate("auth-form") {
            post("/login") {
                call.sessions.set(call.principal<User.SessionUser>())
                call.respondRedirect("/gallery")
            }
        }

        authenticate("auth-session") {
            get("/file/{id}/delete") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id parameter")
                val file = File("./uploads/$id")

                val data = getImageData(id, call.principal())
                if(!(data.containsKey("isManagable") && data["isManagable"] as Boolean)){
                    if((call.principal<User.SessionUser>()?.rLevel ?: 0) < 4)
                        return@get call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete this file")
                }

                if (file.exists()) file.delete()
                call.respondRedirect("/gallery")
            }

            get("/gallery"){
                call.respond(MustacheContent("gallery.hbs", mapOf("" to "")))
            }
        }

        authenticate("auth-basic") {
            post("/upload") {
                val user: User.SessionUser = call.principal() ?: return@post call.respond(HttpStatusCode.Forbidden, "You must be logged in to upload")
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    println(part.name)
                    if(part is PartData.FileItem) {
                        val name = part.originalFileName!!
                        val file = File("./uploads/${user.id}_${System.currentTimeMillis()}.$name")
                        part.streamProvider().use { its ->
                            file.outputStream().buffered().use { its.copyTo(it) }
                        }
                    }
                    part.dispose()
                }
                call.respond("Ok")
            }
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id parameter")
            val result = MustacheContent("image.hbs", mapOf("iname" to id))
            call.respond(result)
        }

        get("/file/{id}"){
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id parameter")
            val file = File("./uploads/$id")
            if(!file.exists()) return@get call.respond(HttpStatusCode.NotFound, "File not found")
            call.respondFile(file)
        }

        get("/i/{id}"){
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id parameter")
            val imgData = getImageData(id, call.sessions.get())
            if(!File("./uploads/$id").exists()) return@get call.respond(HttpStatusCode.NotFound, "File not found")
            call.respond(MustacheContent("ImageView.hbs", imgData))
        }

        static("/") {
            val staticFile = File("./static/")
            staticFile.mkdirs()
            files(staticFile.path)
        }
    }
}