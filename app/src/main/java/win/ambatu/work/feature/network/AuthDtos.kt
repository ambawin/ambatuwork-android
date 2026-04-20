package win.ambatu.work.feature.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleAuthRequest(
    @Json(name = "id_token")
    val idToken: String,
    @Json(name = "device_name")
    val deviceName: String = "android"
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    @Json(name = "avatar_url")
    val avatarUrl: String?
)

@JsonClass(generateAdapter = true)
data class GoogleAuthResponse(
    val token: String,
    @Json(name = "token_type")
    val tokenType: String,
    val user: UserDto
)