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

    @GET("api/v1/projects/{project_id}")
    suspend fun getProject(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long
    ): ProjectResponse

    @PATCH("api/v1/projects/{project_id}")
    suspend fun updateProject(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Body request: UpdateProjectRequest
    ): ProjectResponse

    @GET("api/v1/projects/{project_id}/members")
    suspend fun getProjectMembers(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long
    ): ProjectMemberListResponse

    @PATCH("api/v1/projects/{project_id}/members/{user_id}")
    suspend fun updateProjectMemberRole(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("user_id") userId: Long,
        @Body request: UpdateMemberRoleRequest
    ): ProjectMemberResponse

    @DELETE("api/v1/projects/{project_id}/members/{user_id}")
    suspend fun removeProjectMember(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("user_id") userId: Long
    ): MessageResponse

    @POST("api/v1/projects/{project_id}/invitations")
    suspend fun createInvitation(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Body request: CreateInvitationRequest
    ): InvitationResponse

    @GET("api/v1/invitations")
    suspend fun getInvitations(
        @Header("Authorization") authorization: String
    ): InvitationListResponse

    @POST("api/v1/invitations/{token}/accept")
    suspend fun acceptInvitation(
        @Header("Authorization") authorization: String,
        @Path("token", encoded = true) token: String
    ): AcceptInvitationResponse

    @GET("api/v1/projects/{project_id}/backlog-items")
    suspend fun getProjectBacklogItems(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long
    ): BacklogItemListResponse

    @POST("api/v1/projects/{project_id}/backlog-items")
    suspend fun createBacklogItem(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Body request: CreateBacklogItemRequest
    ): BacklogItemResponse

    @GET("api/v1/projects/{project_id}/backlog-items/{backlog_id}")
    suspend fun getBacklogItem(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("backlog_id") backlogId: Long
    ): BacklogItemResponse

    @GET("api/v1/projects/{project_id}/sprints")
    suspend fun getProjectSprints(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long
    ): SprintListResponse
}
