package win.ambatu.work.data.repository

import win.ambatu.work.feature.network.ApiService
import win.ambatu.work.feature.network.GoogleAuthRequest
import win.ambatu.work.feature.network.GoogleAuthResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun loginWithGoogle(idToken: String): GoogleAuthResponse {
        return apiService.authWithGoogle(
            GoogleAuthRequest(
                idToken = idToken,
                deviceName = "android"
            )
        )
    }

    suspend fun getMe(token: String): win.ambatu.work.feature.network.UserDto {
        val response = apiService.getMe(token)
        return response.data ?: response.user ?: throw Exception("User data not found in response")
    }

    suspend fun logout(token: String): String {
        return apiService.logout("Bearer $token").message
    }
}
