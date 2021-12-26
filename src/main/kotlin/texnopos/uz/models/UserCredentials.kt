package texnopos.uz.models

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt


@Serializable
data class UserCredentials(
    val username: String,
    val password: String
) {
    fun hashedPassword(): String = BCrypt.hashpw(password, BCrypt.gensalt())
    fun isValidCredentials(): Boolean = username.length > 2 && password.length > 5
}