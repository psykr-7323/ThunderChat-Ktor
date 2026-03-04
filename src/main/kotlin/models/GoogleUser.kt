package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUser(
    val id: String,
    val name: String,
    @SerialName("given_name")
    val givenName: String? = null,
    val picture: String? = null
)