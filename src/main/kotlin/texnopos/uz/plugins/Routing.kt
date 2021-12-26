package texnopos.uz.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlinx.serialization.Serializable
import texnopos.uz.routing.authenticationRoutes
import texnopos.uz.routing.noteRoutes
import java.io.File

fun Application.configureRouting() {

    routing {
        noteRoutes()
        authenticationRoutes()
    }
}

