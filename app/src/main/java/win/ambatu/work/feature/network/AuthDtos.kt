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
    val id: Long? = null,
    val name: String? = null,
    val email: String? = null,
    @Json(name = "avatar_url")
    val avatarUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class UserResponse(
    val data: UserDto? = null,
    val user: UserDto? = null
)

@JsonClass(generateAdapter = true)
data class GoogleAuthResponse(
    val token: String,
    @Json(name = "token_type")
    val tokenType: String,
    val user: UserDto
)