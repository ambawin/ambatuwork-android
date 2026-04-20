package win.ambatu.work.feature.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/auth/google")
    suspend fun authWithGoogle(
        @Body request: GoogleAuthRequest
    ): GoogleAuthResponse

    @GET("api/v1/auth/me")
    suspend fun getMe(
        @Header("Authorization") authorization: String
    ): UserResponse
}
