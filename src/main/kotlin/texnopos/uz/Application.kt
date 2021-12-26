package texnopos.uz

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.selects.select
import org.ktorm.database.Database
import org.ktorm.dsl.*
import texnopos.uz.db.DatabaseConnection.database
import texnopos.uz.entities.NoteEntity
import texnopos.uz.plugins.*
import texnopos.uz.utils.TokenManager

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        val config = HoconApplicationConfig(ConfigFactory.load())
        val tokenManager = TokenManager(config)
        install(ContentNegotiation) {
            json()
        }
        install(Authentication) {
            jwt {
                verifier(tokenManager.verifyJWT())
                realm = config.property("realm").getString()
                validate { jwtCredential ->
                    if (jwtCredential.payload.getClaim("username").asString().isNotEmpty())
                        JWTPrincipal(jwtCredential.payload) else null
                }
            }
        }
        configureRouting()
    }.start(wait = true)
}
