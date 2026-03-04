package configurations

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.server.application.*
import io.ktor.http.auth.HttpAuthHeader // 👈 NEW IMPORT 1
import io.ktor.server.auth.parseAuthorizationHeader // 👈 NEW IMPORT 2
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import security.JwtConfig

fun Application.configureSecurity() {
    install(Authentication) {

        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/auth/google/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID") ?: "MISSING_CLIENT_ID",
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET") ?: "MISSING_CLIENT_SECRET",
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
                )
            }
            client = HttpClient(CIO)
        }

        jwt("auth-jwt") {
            realm = "ThunderChat Server"

            authHeader { call ->

                val headerValue = call.request.parseAuthorizationHeader()
                if (headerValue != null) return@authHeader headerValue

                val queryToken = call.request.queryParameters["token"]
                if (queryToken != null) {
                    return@authHeader HttpAuthHeader.Single("Bearer", queryToken)
                }
                return@authHeader null
            }

            verifier(
                com.auth0.jwt.JWT
                    .require(JwtConfig.algorithm)
                    .withAudience(JwtConfig.AUDIENCE)
                    .withIssuer(JwtConfig.ISSUER)
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}