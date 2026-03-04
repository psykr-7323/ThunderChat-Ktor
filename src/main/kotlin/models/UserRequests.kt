package models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)


@Serializable
data class LoginRequest(
    val username: String,
    val password: String

)