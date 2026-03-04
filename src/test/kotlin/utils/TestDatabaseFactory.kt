package utils

import models.Message
import models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object TestDatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"

        Database.connect(jdbcURL, driverClassName)

        transaction {
            SchemaUtils.create(Users, Message)
        }
    }
}