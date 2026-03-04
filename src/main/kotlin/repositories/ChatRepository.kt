package repositories

import models.ChatMessage
import models.Message
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

interface ChatRepository {
    fun getMessageHistory(limit: Int = 50): List<ChatMessage>
    fun saveMessage(text: String, username: String, timestamp: Long)
}

class ChatRepositoryImpl : ChatRepository {
    override fun getMessageHistory(limit: Int): List<ChatMessage> {
        return transaction {
            Message.selectAll()
                .orderBy(Message.timestamp to SortOrder.DESC)
                .limit(limit)
                .map {
                    ChatMessage(
                        text = it[Message.text],
                        username = it[Message.sender],
                        timestamp = it[Message.timestamp]
                    )
                }
                .reversed()
        }
    }

    override fun saveMessage(text: String, username: String, timestamp: Long) {
        transaction {
            Message.insert {
                it[Message.text] = text
                it[Message.sender] = username
                it[Message.timestamp] = timestamp
            }
        }
    }
}