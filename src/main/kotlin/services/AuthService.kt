package services

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import models.GoogleUser
import repositories.UserRepository
import security.JwtConfig

class AuthService(private val userRepository: UserRepository) {

    private val httpClient = HttpClient(CIO)
    private val parser = Json { ignoreUnknownKeys = true }

    suspend fun processGoogleLogin(accessToken: String): String? {
        return try {
            val response = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }

            val userInfoString = response.bodyAsText()
            val googleUser = parser.decodeFromString<GoogleUser>(userInfoString)

            val existingUser = userRepository.findByUsername(googleUser.name)

            if (existingUser == null) {
                userRepository.createUser(
                    username = googleUser.name,
                    password = "GOOGLE_OAUTH_ACCOUNT",
                    email = "${googleUser.id}@google.com"
                )
            }

            JwtConfig.generateToken(googleUser.name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}