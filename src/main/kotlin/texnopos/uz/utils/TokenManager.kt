package texnopos.uz.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.config.*
import texnopos.uz.models.User
import java.util.*

class TokenManager(private val config: HoconApplicationConfig) {

    companion object {
        const val MINUTE = 60_000
    }
    val audience = config.property("audience").getString()
    val issuer = config.property("issuer").getString()
    val secret = config.property("secret").getString()
    val expirationDate = System.currentTimeMillis() + 5 * MINUTE

    fun generateJWT(user: User): String {
        val audience = config.property("audience").getString()
        val issuer = config.property("issuer").getString()
        val secret = config.property("secret").getString()
        val expirationDate = System.currentTimeMillis() + 5 * MINUTE

        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(Date(expirationDate))
            .withClaim("username", user.username)
            .withClaim("userId", user.id)
            .sign(Algorithm.HMAC256(secret))
    }

    fun verifyJWT():JWTVerifier{

        return JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
}