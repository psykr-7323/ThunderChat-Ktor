package routes


import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.LoginRequest
import models.RegisterRequest
import org.koin.ktor.ext.inject
import services.UserService

fun Route.userRoutes() {
    val userService by inject<UserService>()
    route("/register") {
        post {
            val request = call.receive<RegisterRequest>()
            if (request.password.length < 6) {
                call.respond(HttpStatusCode.BadRequest, "Password must be at least 6 characters")
                return@post
            }
            val success = userService.register(request.username, request.password, request.email)
            if (success) {
                call.respond(HttpStatusCode.Created, "User created successfully")
            } else {
                call.respond(HttpStatusCode.Conflict, "Username or Email already exists.")
            }
        }
    }

    route("/login"){
        post {
            val request = call.receive<LoginRequest>()
            val token = userService.authenticate(request.username, request.password)

            if (token != null){
                call.respond(HttpStatusCode.OK, mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
            }
        }
    }
}