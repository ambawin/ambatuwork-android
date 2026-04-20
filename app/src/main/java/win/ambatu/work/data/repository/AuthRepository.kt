package win.ambatu.work.data.repository

import win.ambatu.work.feature.network.ApiService
import win.ambatu.work.feature.network.GoogleAuthRequest
import win.ambatu.work.feature.network.GoogleAuthResponse

class AuthRepository(
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
        return apiService.getMe(token)
    }
}
