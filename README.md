# ThunderChat

A high-performance, real-time chat server built with Ktor. This project follows Clean Architecture principles, utilizing Koin for dependency injection, JetBrains Exposed for database management, and comprehensive automated testing.

## 🚀 Key Features

* **Clean Architecture:** Strictly decoupled layers separating routes, business logic, and database operations.
* **Real-Time WebSockets:** Bi-directional, real-time messaging pipeline.
* **Advanced Security:** JWT-based authentication paired with Google OAuth 2.0 integration.
* **Dependency Injection:** Automated component wiring using Koin.
* **Data Persistence:** PostgreSQL database managed via JetBrains Exposed ORM.
* **Automated QA:** Full integration test suite utilizing an in-memory H2 database.

## 🛠 Tech Stack

* **Language:** Kotlin
* **Framework:** Ktor 3.x
* **Dependency Injection:** Koin
* **Database:** PostgreSQL (Production) & H2 (Testing)
* **ORM:** JetBrains Exposed
* **Authentication:** JWT & Google OAuth 2.0

## 🏗 Architecture Layout

Our codebase is divided into clear, specialized layers:

* **`routes/`:** The API surface handling HTTP requests and WebSocket connections.
* **`services/`:** The core business logic, data transformation, and security validations.
* **`repositories/`:** The data access layer directly communicating with the database.
* **`di/`:** Koin modules defining how all components are injected.
* **`models/`:** Kotlin data classes and Exposed table definitions.

## 🚦 Building & Running

Use the following Gradle tasks to manage our project:

| Task | Description |
| :--- | :--- |
| `./gradlew run` | Run our server locally on `http://0.0.0.0:8080` |
| `./gradlew test` | Execute the automated integration test suite |
| `./gradlew build` | Compile the entire project |
| `./gradlew buildFatJar` | Build an executable JAR including all dependencies |

If the server boots successfully, the console will display:

```text
2026-03-04 23:45:12.123 [main] INFO  Application - Application started in 0.250 seconds.
2026-03-04 23:45:12.125 [main] INFO  Application - Responding at [http://0.0.0.0:8080](http://0.0.0.0:8080)