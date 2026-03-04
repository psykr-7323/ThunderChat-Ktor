package models

import org.jetbrains.exposed.sql.Table


data class User(
    val username: String,
    val password: String,
    val email: String
)

object Users : Table(){
    val username = varchar("username", 50)
    val password = varchar("password", 128)
    val email = varchar("email", 100).uniqueIndex()
    override val primaryKey = PrimaryKey(username)
}