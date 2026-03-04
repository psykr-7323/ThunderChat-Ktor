package repositories

import models.User
import models.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

interface UserRepository {
    fun findByUsername(username: String): User?
    fun createUser(username: String, password: String, email: String): Boolean
}

class UserRepositoryImpl : UserRepository {
    override fun findByUsername(username: String): User? {
        return transaction {
            val row = Users.selectAll().where { Users.username eq username }.singleOrNull()
            if (row != null) {
                User(
                    username = row[Users.username],
                    password = row[Users.password],
                    email = row[Users.email]
                )
            } else {
                null
            }
        }
    }

    override fun createUser(username: String, password: String, email: String): Boolean {
        return try {
            transaction {
                Users.insert {
                    it[Users.username] = username
                    it[Users.password] = password
                    it[Users.email] = email
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}