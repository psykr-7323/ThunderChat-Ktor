package models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val text: String,
    val username: String,
    val timestamp: Long = System.currentTimeMillis()
)
