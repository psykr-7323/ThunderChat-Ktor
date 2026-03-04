package security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private const val SECRET = "secret-key"
    const val ISSUER = "http://localhost:8080/"
    const val AUDIENCE = "thunderchat-users"
    private const val VALIDITY_IN_MS = 36_000_000L

    val algorithm = Algorithm.HMAC512(SECRET)

    fun generateToken(username: String): String {
        return JWT.create()
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .withClaim("username", username)
            .withExpiresAt(getExpiration())
            .sign(algorithm)
    }

    private fun getExpiration() = Date(System.currentTimeMillis() + VALIDITY_IN_MS)
}