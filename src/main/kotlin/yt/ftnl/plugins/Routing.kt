package yt.ftnl.plugins

import com.google.gson.Gson
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
import yt.ftnl.CONFIG
import yt.ftnl.core.database.structures.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manage routes
 */
fun Application.configureRouting() {
    install(AutoHeadResponse)

    routing {
        fun checkId(id: String?): List<String>? {
            val e = id?.split("_")
            return if (e?.size != 2) null else e
        }
        fun getImageData(imgName: String, session: User.SessionUser?): Map<String, Any?> {
            val regex = Regex("^(\\d+)_(\\d+)\\.(\\w+)$")
            regex.matchEntire(imgName) ?: return mapOf("error" to "Invalid image name")
            val (u, i) = checkId(imgName) ?: return mapOf("error" to "Invalid image name")
            val staffId = u.toIntOrNull() ?: 0
            val fileData = i.split(".")
            val date = fileData[0].toLongOrNull() ?: 0
            val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
            val netDate = Date(date)
            val ext = fileData[1]
            val staff = if (session?.id != staffId) User.getByUid(staffId)?.toSessionUser() else session

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
                val (u, i) = checkId(id) ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid id parameter")
                val file = File("./uploads/$u/$i")

                val data = getImageData(id, call.principal())
                if(!(data.containsKey("isManagable") && data["isManagable"] as Boolean)){
                    if((call.principal<User.SessionUser>()?.rLevel ?: 0) < 4)
                        return@get call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete this file")
                }

                if (file.exists()) file.delete()
                call.respondRedirect("/gallery?withDeleted=$id")
            }

            get("/gallery"){
                val query = call.request.queryParameters["withDeleted"]
                val session = call.principal<User.SessionUser>() ?: return@get call.respond(HttpStatusCode.Unauthorized, "You must be logged in to view this page")
                val userFolder = File("./uploads/${session.id}")
                userFolder.mkdirs()
                var images = userFolder.listFiles()?.map { "${session.id}_${it.name}" } ?: listOf()

                if (query != null) images = images.filter { it != query }

                call.respond(MustacheContent("gallery.hbs", mapOf("images" to Gson().toJson(images))))
            }
        }

        authenticate("auth-basic") {
            post("/upload") {
                val user: User.SessionUser = call.principal() ?: return@post call.respond(HttpStatusCode.Forbidden, "You must be logged in to upload")
                val multipart = call.receiveMultipart()
                val time = System.currentTimeMillis()

                multipart.forEachPart { part ->
                    if(part is PartData.FileItem) {
                        val name = part.originalFileName!!.split(".").last()
                        File("./uploads/${user.id}").mkdirs()
                        val file = File("./uploads/${user.id}/$time.$name")
                        part.streamProvider().use { its ->
                            file.outputStream().buffered().use { its.copyTo(it) }
                        }
                        call.respond( "${CONFIG.webCfg.serverAddress}/${user.id}_$time.$name")
                    }
                    part.dispose()
                }
            }
        }


        get("/{id}") {
            val id = checkId(call.parameters["id"]) ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id parameter")
            val result = MustacheContent("image.hbs", mapOf("iname" to id))
            call.respond(result)
        }

        get("/file/{id}"){
            val id = checkId(call.parameters["id"]) ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid id parameter")
            val (u, i) = id
            val file = File("./uploads/$u/$i")
            if(!file.exists()) return@get call.respond(HttpStatusCode.NotFound, "File not found")
            call.respondFile(file)
        }

        get("/i/{id}"){
            val id = checkId(call.parameters["id"]) ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid id parameter")
            val (u, i) = id
            val imgData = getImageData(id.joinToString("_"), call.sessions.get())
            if(!File("./uploads/$u/$i").exists()) return@get call.respond(HttpStatusCode.NotFound, "File not found")
            call.respond(MustacheContent("ImageView.hbs", imgData))
        }

        static("/") {
            val staticFile = File("./static/")
            staticFile.mkdirs()
            files(staticFile.path)
        }
    }
}