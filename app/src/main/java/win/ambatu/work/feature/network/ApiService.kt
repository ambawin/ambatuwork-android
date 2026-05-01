package win.ambatu.work.feature.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/v1/auth/google")
    suspend fun authWithGoogle(
        @Body request: GoogleAuthRequest
    ): GoogleAuthResponse

    @GET("api/v1/auth/me")
    suspend fun getMe(
        @Header("Authorization") authorization: String
    ): UserResponse

    @GET("api/v1/projects")
    suspend fun getProjects(
        @Header("Authorization") authorization: String
    ): ProjectListResponse

    @POST("api/v1/projects")
    suspend fun createProject(
        @Header("Authorization") authorization: String,
        @Body request: CreateProjectRequest
    ): ProjectResponse

    @GET("api/v1/projects/{projectId}")
    suspend fun getProject(
        @Header("Authorization") authorization: String,
        @Path("projectId") projectId: Long
    ): ProjectResponse

    @PATCH("api/v1/projects/{projectId}")
    suspend fun updateProject(
        @Header("Authorization") authorization: String,
        @Path("projectId") projectId: Long,
        @Body request: UpdateProjectRequest
    ): ProjectResponse

    @GET("api/v1/projects/{projectId}/members")
    suspend fun getProjectMembers(
        @Header("Authorization") authorization: String,
        @Path("projectId") projectId: Long
    ): ProjectMemberListResponse

    @PATCH("api/v1/projects/{projectId}/members/{userId}")
    suspend fun updateProjectMemberRole(
        @Header("Authorization") authorization: String,
        @Path("projectId") projectId: Long,
        @Path("userId") userId: Long,
        @Body request: UpdateMemberRoleRequest
    ): ProjectMemberResponse

    @DELETE("api/v1/projects/{projectId}/members/{userId}")
    suspend fun removeProjectMember(
        @Header("Authorization") authorization: String,
        @Path("projectId") projectId: Long,
        @Path("userId") userId: Long
    ): MessageResponse

    @POST("api/v1/projects/{projectId}/invitations")
    suspend fun createInvitation(
        @Header("Authorization") authorization: String,
        @Path("projectId") projectId: Long,
        @Body request: CreateInvitationRequest
    ): InvitationResponse

    @POST("api/v1/invitations/{token}/accept")
    suspend fun acceptInvitation(
        @Header("Authorization") authorization: String,
        @Path("token", encoded = true) token: String
    ): AcceptInvitationResponse
}
