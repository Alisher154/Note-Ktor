package texnopos.uz.routing

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt
import texnopos.uz.db.DatabaseConnection
import texnopos.uz.entities.UserEntity
import texnopos.uz.models.NoteResponse
import texnopos.uz.models.User
import texnopos.uz.models.UserCredentials
import texnopos.uz.utils.TokenManager

fun Application.authenticationRoutes() {
    val db = DatabaseConnection.database
    val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

    routing {

        post("/register") {
            val userCredentials = call.receive<UserCredentials>()

            if (!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(
                        success = false,
                        message = "Username greater than 2 and password greater than 6",
                        data = ""
                    )
                )
                return@post
            }
            val username = userCredentials.username.lowercase()
            val password = userCredentials.hashedPassword()

            //User validation
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map { it[UserEntity.username] }
                .firstOrNull()
            if (user != null) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(
                        success = false,
                        message = "User already exists, please try a different username",
                        data = ""
                    )
                )
                return@post
            }

            db.insert(UserEntity) {
                set(it.username, username)
                set(it.password, password)
            }

            call.respond(
                HttpStatusCode.Created,
                NoteResponse(
                    success = true,
                    message = "User successfully created",
                    data = ""
                )
            )
        }

        post("/login") {

            val userCredentials = call.receive<UserCredentials>()

            if (!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(
                        success = false,
                        message = "Username greater than 2 and password greater than 6",
                        data = ""
                    )
                )
                return@post
            }
            val username = userCredentials.username.lowercase()
            val password = userCredentials.password
            //Check user exists
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map {
                    val id = it[UserEntity.id]!!
                    val username2 = it[UserEntity.username]!!
                    val password2 = it[UserEntity.password]!!
                    User(id, username2, password2)
                }
                .firstOrNull()
            if (user == null) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(
                        success = false,
                        message = "Invalid username or password.",
                        data = ""
                    )
                )
                return@post
            }
            val doesPasswordMatch = BCrypt.checkpw(password, user.password)
            if (!doesPasswordMatch) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(
                        success = false,
                        message = "Invalid username or password.",
                        data = ""
                    )
                )
                return@post
            }

            val token = tokenManager.generateJWT(user)
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(
                    success = true,
                    message = "User successfully logged in",
                    data = token
                )
            )
        }
        authenticate {
            get("/me"){
                val principal=call.principal<JWTPrincipal>()!!
                val username=principal.payload.getClaim("username").asString()
                val userId=principal.payload.getClaim("userId").asInt()
                call.respondText("Hello $username with id: $userId")
            }
        }
    }
}