package services

import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import models.ChatMessage
import repositories.ChatRepository
import java.util.*

class ChatService(private val chatRepository: ChatRepository) {
    private val connections = Collections.synchronizedSet(LinkedHashSet<DefaultWebSocketSession>())

    fun addConnection(session: DefaultWebSocketSession) {
        connections.add(session)
    }

    fun removeConnection(session: DefaultWebSocketSession) {
        connections.remove(session)
    }

    suspend fun sendHistory(session: DefaultWebSocketSession) {
        val history = chatRepository.getMessageHistory()
        history.forEach { message ->
            val json = Json.encodeToString(message)
            session.send(json)
        }
    }

    suspend fun processAndBroadcast(receivedText: String, authenticatedUsername: String) {
        try {
            val userMessage = Json.decodeFromString<ChatMessage>(receivedText)
            val currentTime = System.currentTimeMillis()

            chatRepository.saveMessage(userMessage.text, authenticatedUsername, currentTime)

            val finalMessage = userMessage.copy(
                username = authenticatedUsername,
                timestamp = currentTime
            )

            val jsonToSend = Json.encodeToString(finalMessage)

            connections.forEach { session ->
                session.send(jsonToSend)
            }
        } catch (e: Exception) {
            println("Error parsing JSON: ${e.localizedMessage}")
        }
    }
}