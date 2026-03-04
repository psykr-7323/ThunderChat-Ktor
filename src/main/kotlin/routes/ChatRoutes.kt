package routes

import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject
import services.ChatService

fun Route.chatRoutes() {
    val chatService by inject<ChatService>()

    authenticate("auth-jwt") {
        webSocket("/chat") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal?.payload?.getClaim("username")?.asString() ?: "Unknown"

            println("$username has entered our chat!")

            chatService.addConnection(this)

            try {
                chatService.sendHistory(this)

                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        chatService.processAndBroadcast(receivedText, username)
                    }
                }
            } finally {
                println("Removing User!")
                chatService.removeConnection(this)
            }
        }
    }
}