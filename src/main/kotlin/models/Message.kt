package models

import org.jetbrains.exposed.sql.Table

object Message : Table() {
    val id = integer("id").autoIncrement()
    val text = text("text")
    val sender = varchar("sender", 50).references(Users.username)
    val timestamp = long("timestamp")
    override val primaryKey = PrimaryKey(id)
}