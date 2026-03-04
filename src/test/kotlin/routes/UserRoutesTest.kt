package routes

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.insert
import models.Users
import configurations.configureRouting
import configurations.configureSecurity
import configurations.configureSerialization
import configurations.configureSockets
import di.appModule
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.install
import io.ktor.server.testing.testApplication
import io.ktor.websocket.*
import org.koin.ktor.plugin.Koin
import security.JwtConfig
import utils.TestDatabaseFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRoutesTest {

    @Test
    fun testSuccessfulRegistration() {
        TestDatabaseFactory.init()

        testApplication {
            application {
                this@application.install(Koin) {
                    modules(appModule)
                }
                configureSecurity()
                configureSerialization()
                configureSockets()
                configureRouting()
            }

            val response = client.post("/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "new_robot", "password": "secure_password123", "email": "new_robot@test.com"}""")
            }

            assertEquals(HttpStatusCode.Created, response.status)
        }
    }

    @Test
    fun testDuplicateRegistrationFails() {
        TestDatabaseFactory.init()

        transaction {
            Users.insert {
                it[username] = "taken_robot"
                it[password] = "existing_password"
                it[email] = "taken@test.com"
            }
        }

        testApplication {
            application {
                this@application.install(Koin) {
                    modules(appModule)
                }
                configureSecurity()
                configureSerialization()
                configureSockets()
                configureRouting()
            }

            val response = client.post("/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "taken_robot", "password": "new_password"}""")
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun testInvalidLoginReturnsUnauthorized() {
        TestDatabaseFactory.init()

        testApplication {
            application {
                this@application.install(Koin) {
                    modules(appModule)
                }
                configureSecurity()
                configureSerialization()
                configureSockets()
                configureRouting()
            }

            val response = client.post("/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "ghost_user", "password": "wrong_password"}""")
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun testMissingTokenRejectsWebSocketConnection() {
        TestDatabaseFactory.init()

        testApplication {
            application {
                this@application.install(Koin) {
                    modules(appModule)
                }
                configureSecurity()
                configureSerialization()
                configureSockets()
                configureRouting()
            }

            val wsClient = createClient {
                install(WebSockets)
            }

            try {
                wsClient.webSocket("/chat") {
                }
                assertTrue(false)
            } catch (e: Exception) {
                assertTrue(true)
            }
        }
    }

    @Test
    fun testValidTokenAllowsWebSocketEcho() {
        TestDatabaseFactory.init()
        transaction {
            Users.insert {
                it[username] = "RobotTester"
                it[password] = "robot_password"
                it[email] = "robot@test.com"
            }
        }

        testApplication {
            application {
                this@application.install(Koin) {
                    modules(appModule)
                }
                configureSecurity()
                configureSerialization()
                configureSockets()
                configureRouting()
            }

            val wsClient = createClient {
                install(WebSockets)
            }

            val validToken = JwtConfig.generateToken("RobotTester")

            wsClient.webSocket("/chat?token=$validToken") {
                val payload = """{"text": "Automated message!", "username": "RobotTester", "timestamp": 0}"""
                send(Frame.Text(payload))

                val incomingFrame = incoming.receive() as Frame.Text
                val responseText = incomingFrame.readText()

                assertTrue(responseText.contains("Automated message!"))
                assertTrue(responseText.contains("RobotTester"))
            }
        }
    }
}