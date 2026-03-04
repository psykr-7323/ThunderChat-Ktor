package configurations

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import routes.authRoutes
import routes.chatRoutes
import routes.userRoutes

fun Application.configureRouting(){
    routing {
        userRoutes()
        chatRoutes()
        authRoutes()
    }
}


