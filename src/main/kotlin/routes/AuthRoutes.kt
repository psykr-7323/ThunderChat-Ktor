package routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import services.AuthService

fun Route.authRoutes() {

    val authService by inject<AuthService>()

    authenticate("auth-oauth-google") {
        get("/login") {
        }

        get("/auth/google/callback") {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()

            if (principal != null) {
                val token = authService.processGoogleLogin(principal.accessToken)

                if (token != null) {
                    call.respond(HttpStatusCode.OK, mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve user from Google.")
                }
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Login failed.")
            }
        }
    }
}