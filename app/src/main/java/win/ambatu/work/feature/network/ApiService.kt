package win.ambatu.work.feature.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): MessageResponse

    @PUT("api/v1/auth/device-token")
    suspend fun updateDeviceToken(
        @Header("Authorization") authorization: String,
        @Body request: DeviceTokenRequest
    ): MessageResponse

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

    @PATCH("api/v1/projects/{project_id}/backlog-items/{backlog_id}")
    suspend fun updateBacklogItem(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("backlog_id") backlogId: Long,
        @Body request: UpdateBacklogItemRequest
    ): BacklogItemResponse

    @DELETE("api/v1/projects/{project_id}/backlog-items/{backlog_id}")
    suspend fun archiveBacklogItem(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("backlog_id") backlogId: Long
    ): BacklogItemResponse

    @GET("api/v1/projects/{project_id}/sprints")
    suspend fun getProjectSprints(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long
    ): SprintListResponse

    @POST("api/v1/projects/{project_id}/sprints")
    suspend fun createSprint(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Body request: CreateSprintRequest
    ): SprintResponse

    @GET("api/v1/projects/{project_id}/sprints/{sprint_id}/board")
    suspend fun getSprintBoard(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): SprintBoardResponse

    @GET("api/v1/projects/{project_id}/sprints/{sprint_id}")
    suspend fun getSprint(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): SprintResponse

    @POST("api/v1/projects/{project_id}/sprints/{sprint_id}/start")
    suspend fun startSprint(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): SprintResponse

    @POST("api/v1/projects/{project_id}/sprints/{sprint_id}/close")
    suspend fun closeSprint(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): CloseSprintResponse

    @GET("api/v1/projects/{project_id}/sprints/{sprint_id}/review")
    suspend fun getSprintReview(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): SprintReviewResponse

    @POST("api/v1/projects/{project_id}/sprints/{sprint_id}/review")
    suspend fun submitSprintReview(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long,
        @Body request: SubmitSprintReviewRequest
    ): SprintReviewResponse

    @GET("api/v1/projects/{project_id}/sprints/{sprint_id}/checkins")
    suspend fun getDailyCheckins(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): DailyCheckinListResponse

    @POST("api/v1/projects/{project_id}/sprints/{sprint_id}/checkins")
    suspend fun submitDailyCheckin(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long,
        @Body request: SubmitDailyCheckinRequest
    ): DailyCheckinResponse

    // ===================== Retrospective API =====================

    @GET("api/v1/projects/{project_id}/sprints/{sprint_id}/retrospective")
    suspend fun getRetrospective(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): RetrospectiveResponse

    @POST("api/v1/projects/{project_id}/sprints/{sprint_id}/retrospective")
    suspend fun submitHappinessScore(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long,
        @Body request: SubmitHappinessRequest
    ): RetrospectiveResponse

    @POST("api/v1/projects/{project_id}/sprints/{sprint_id}/retrospective/items")
    suspend fun createRetroItem(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long,
        @Body request: CreateRetroItemRequest
    ): RetroItemResponse

    @DELETE("api/v1/projects/{project_id}/sprints/{sprint_id}/retrospective/items/{item_id}")
    suspend fun deleteRetroItem(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long,
        @Path("item_id") itemId: Long
    ): MessageResponse

    // ===================== Peer Review API =====================

    @GET("api/v1/projects/{project_id}/sprints/{sprint_id}/peer-review-cycle")
    suspend fun getPeerReviewCycle(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): PeerReviewCycleResponse

    @POST("api/v1/projects/{project_id}/sprints/{sprint_id}/peer-review-cycle")
    suspend fun openPeerReviewCycle(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("sprint_id") sprintId: Long
    ): PeerReviewCycleResponse

    @POST("api/v1/projects/{project_id}/peer-review-cycles/{cycle_id}/close")
    suspend fun closePeerReviewCycle(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("cycle_id") cycleId: Long
    ): PeerReviewCycleResponse

    @POST("api/v1/projects/{project_id}/peer-review-cycles/{cycle_id}/reviews")
    suspend fun submitPeerReview(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("cycle_id") cycleId: Long,
        @Body request: SubmitPeerReviewRequest
    ): PeerReviewResponse

    @GET("api/v1/projects/{project_id}/peer-review-cycles/{cycle_id}/summary")
    suspend fun getPeerReviewCycleSummary(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("cycle_id") cycleId: Long
    ): PeerReviewSummaryResponse

    @GET("api/v1/projects/{project_id}/peer-review-cycles/{cycle_id}/my-summary")
    suspend fun getMyPeerReviewSummary(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long,
        @Path("cycle_id") cycleId: Long
    ): PeerReviewMySummaryResponse

    @GET("api/v1/users/me/stats")
    suspend fun getUserStats(
        @Header("Authorization") authorization: String
    ): UserStatsResponse

    @GET("api/v1/projects/{project_id}/stats")
    suspend fun getProjectStats(
        @Header("Authorization") authorization: String,
        @Path("project_id") projectId: Long
    ): ProjectStatsResponse
}

