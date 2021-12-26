package texnopos.uz.routing

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.dsl.*
import texnopos.uz.db.DatabaseConnection
import texnopos.uz.entities.NoteEntity
import texnopos.uz.models.Note
import texnopos.uz.models.NoteRequest
import texnopos.uz.models.NoteResponse

fun Application.noteRoutes() {
    val db = DatabaseConnection.database
    routing {

        post("/add") {
            val request = call.receive<NoteRequest>()
            val result = db.insert(NoteEntity) {
                set(it.note, request.note)
            }
            if (result == 1) {
                call.respond(
                    HttpStatusCode.OK, NoteResponse(
                        success = true,
                        message = "Note has been successfully inserted",
                        data = ""
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, NoteResponse(
                        success = false,
                        message = "Failed to insert note.",
                        data = ""
                    )
                )
            }
        }

        get("/notes") {
            val notes = db.from(NoteEntity).select().map {
                val id = it[NoteEntity.id]
                val note = it[NoteEntity.note]
                Note(id ?: -1, note ?: "")
            }
            call.respond(notes)
        }

        get("/notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val note = db.from(NoteEntity).select()
                .where { NoteEntity.id eq id }
                .map {
                    val id2 = it[NoteEntity.id]!!
                    val note = it[NoteEntity.note]!!
                    Note(id2, note)
                }.firstOrNull()
            if (note != null) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(
                        success = true,
                        message = "Successfully",
                        data = note
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    NoteResponse(
                        success = false,
                        message = "Could not found note with id = $id",
                        data = ""
                    )
                )
            }
        }

        put("/notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val updatedNote = call.receive<NoteRequest>()
            val rowEffected = db.update(NoteEntity) {
                set(it.note, updatedNote.note)
                where {
                    it.id eq id
                }
            }
            if (rowEffected == 1) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(
                        success = true,
                        message = "Note has been updated",
                        data = ""
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        message = "Note fails to updated",
                        data = ""
                    )
                )
            }
        }

        delete("/notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val rowEffected = db.delete(NoteEntity) {
                it.id eq id
            }
            if (rowEffected == 1) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(
                        success = true,
                        message = "Note has been deleted",
                        data = ""
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        message = "Note fails to delete",
                        data = ""
                    )
                )
            }
        }

    }
}