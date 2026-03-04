package configurations

import io.ktor.server.application.Application
import io.ktor.server.application.log
import models.Message
import models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase(){
    Database.connect(
            url = "jdbc:postgresql://localhost:5432/thunderchat",
            driver = "org.postgresql.Driver",
            user = "psyduck",
            password = "7323"
    )
    transaction {
            SchemaUtils.create(Users, Message)
    }
    log.info("Database connected")
}