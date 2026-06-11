package win.ambatu.work.data.repository

import win.ambatu.work.feature.network.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val apiService: ApiService,
    private val definitionOfDoneApi: win.ambatu.work.generated.api.DefinitionOfDoneApi
) {
    private fun getAuthHeader(token: String) = "Bearer $token"

    suspend fun getProjects(token: String): List<ProjectDto> {
        return apiService.getProjects(getAuthHeader(token)).data
    }

    suspend fun createProject(token: String, request: CreateProjectRequest): ProjectDto {
        return apiService.createProject(getAuthHeader(token), request).data
    }

    suspend fun getProject(token: String, projectId: Long): ProjectDto {
        return apiService.getProject(getAuthHeader(token), projectId).data
    }

    suspend fun updateProject(token: String, projectId: Long, request: UpdateProjectRequest): ProjectDto {
        return apiService.updateProject(getAuthHeader(token), projectId, request).data
    }

    suspend fun getProjectMembers(token: String, projectId: Long): List<ProjectMemberDto> {
        return apiService.getProjectMembers(getAuthHeader(token), projectId).data
    }

    suspend fun updateProjectMemberRole(
        token: String,
        projectId: Long,
        userId: Long,
        role: String
    ): ProjectMemberDto {
        return apiService.updateProjectMemberRole(
            getAuthHeader(token),
            projectId,
            userId,
            UpdateMemberRoleRequest(role)
        ).data
    }

    suspend fun removeProjectMember(token: String, projectId: Long, userId: Long): String {
        return apiService.removeProjectMember(getAuthHeader(token), projectId, userId).message
    }

    suspend fun createInvitation(
        token: String,
        projectId: Long,
        email: String,
        role: String
    ): ProjectInvitationDto {
        val response = apiService.createInvitation(
            getAuthHeader(token),
            projectId,
            CreateInvitationRequest(email, role)
        )
        return response.data
    }

    suspend fun getInvitations(token: String): List<ProjectInvitationDto> {
        return apiService.getInvitations(getAuthHeader(token)).data
    }

    suspend fun acceptInvitation(token: String, invitationToken: String): ProjectDto {
        val response = apiService.acceptInvitation(getAuthHeader(token), invitationToken)
        return response.data
    }

    suspend fun getProjectBacklogItems(token: String, projectId: Long): List<BacklogItemDto> {
        return apiService.getProjectBacklogItems(getAuthHeader(token), projectId).data
    }

    suspend fun createBacklogItem(
        token: String,
        projectId: Long,
        request: CreateBacklogItemRequest
    ): BacklogItemDto {
        return apiService.createBacklogItem(getAuthHeader(token), projectId, request).data
    }

    suspend fun getBacklogItem(token: String, projectId: Long, backlogId: Long): BacklogItemDto {
        return apiService.getBacklogItem(getAuthHeader(token), projectId, backlogId).data
    }

    suspend fun updateBacklogItem(
        token: String,
        projectId: Long,
        backlogId: Long,
        request: UpdateBacklogItemRequest
    ): BacklogItemDto {
        return apiService.updateBacklogItem(getAuthHeader(token), projectId, backlogId, request).data
    }

    suspend fun archiveBacklogItem(token: String, projectId: Long, backlogId: Long): BacklogItemDto {
        return apiService.archiveBacklogItem(getAuthHeader(token), projectId, backlogId).data
    }

    suspend fun getProjectSprints(token: String, projectId: Long): List<SprintDto> {
        return apiService.getProjectSprints(getAuthHeader(token), projectId).data
    }

    suspend fun createSprint(
        token: String,
        projectId: Long,
        request: CreateSprintRequest
    ): SprintDto {
        return apiService.createSprint(getAuthHeader(token), projectId, request).data
    }

    suspend fun getSprintBoard(token: String, projectId: Long, sprintId: Long): SprintBoardDto {
        return apiService.getSprintBoard(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun getSprint(token: String, projectId: Long, sprintId: Long): SprintDto {
        return apiService.getSprint(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun startSprint(token: String, projectId: Long, sprintId: Long): SprintDto {
        return apiService.startSprint(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun closeSprint(token: String, projectId: Long, sprintId: Long): SprintDto {
        return apiService.closeSprint(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun getSprintReview(token: String, projectId: Long, sprintId: Long): SprintReviewDto {
        return apiService.getSprintReview(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun submitSprintReview(
        token: String,
        projectId: Long,
        sprintId: Long,
        request: SubmitSprintReviewRequest
    ): SprintReviewDto {
        return apiService.submitSprintReview(getAuthHeader(token), projectId, sprintId, request).data
    }

    suspend fun getDailyCheckins(token: String, projectId: Long, sprintId: Long): List<DailyCheckinDto> {
        return apiService.getDailyCheckins(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun submitDailyCheckin(
        token: String,
        projectId: Long,
        sprintId: Long,
        request: SubmitDailyCheckinRequest
    ): DailyCheckinDto {
        return apiService.submitDailyCheckin(getAuthHeader(token), projectId, sprintId, request).data
    }

    // ===================== Retrospective =====================

    suspend fun getRetrospective(token: String, projectId: Long, sprintId: Long): RetrospectiveDto {
        return apiService.getRetrospective(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun submitHappinessScore(
        token: String,
        projectId: Long,
        sprintId: Long,
        score: Int
    ): RetrospectiveDto {
        return apiService.submitHappinessScore(
            getAuthHeader(token), projectId, sprintId, SubmitHappinessRequest(score)
        ).data
    }

    suspend fun createRetroItem(
        token: String,
        projectId: Long,
        sprintId: Long,
        request: CreateRetroItemRequest
    ): RetroItemDto {
        return apiService.createRetroItem(getAuthHeader(token), projectId, sprintId, request).data
    }

    suspend fun deleteRetroItem(
        token: String,
        projectId: Long,
        sprintId: Long,
        itemId: Long
    ): String {
        return apiService.deleteRetroItem(getAuthHeader(token), projectId, sprintId, itemId).message
    }

    // ===================== Peer Review =====================

    suspend fun getPeerReviewCycle(token: String, projectId: Long, sprintId: Long): PeerReviewCycleDto {
        return apiService.getPeerReviewCycle(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun openPeerReviewCycle(token: String, projectId: Long, sprintId: Long): PeerReviewCycleDto {
        return apiService.openPeerReviewCycle(getAuthHeader(token), projectId, sprintId).data
    }

    suspend fun closePeerReviewCycle(token: String, projectId: Long, cycleId: Long): PeerReviewCycleDto {
        return apiService.closePeerReviewCycle(getAuthHeader(token), projectId, cycleId).data
    }

    suspend fun submitPeerReview(
        token: String,
        projectId: Long,
        cycleId: Long,
        request: SubmitPeerReviewRequest
    ): PeerReviewDto {
        return apiService.submitPeerReview(getAuthHeader(token), projectId, cycleId, request).data
    }

    suspend fun getPeerReviewCycleSummary(
        token: String,
        projectId: Long,
        cycleId: Long
    ): List<PeerReviewSummaryItemDto> {
        return apiService.getPeerReviewCycleSummary(getAuthHeader(token), projectId, cycleId).data
    }

    suspend fun getMyPeerReviewSummary(
        token: String,
        projectId: Long,
        cycleId: Long
    ): PeerReviewSummaryItemDto {
        return apiService.getMyPeerReviewSummary(getAuthHeader(token), projectId, cycleId).data
    }

    suspend fun getProjectStats(token: String, projectId: Long): win.ambatu.work.feature.network.ProjectStatsDto {
        return apiService.getProjectStats(getAuthHeader(token), projectId).data
    }

    suspend fun updateDefinitionOfDone(projectId: Long, checklist: List<String>): DefinitionOfDoneDto? {
        val request = win.ambatu.work.generated.model.Model5250bdafe024c00ee90bfc650a2d12ccRequest(
            title = "Definition of Done",
            checklist = checklist
        )
        val response = definitionOfDoneApi.call5250bdafe024c00ee90bfc650a2d12cc(projectId.toInt(), request)
        if (response.isSuccessful) {
            val body = response.body()
            val data = body?.data
            if (data != null) {
                return DefinitionOfDoneDto(
                    id = data.id?.toLong() ?: 0L,
                    projectId = data.projectId?.toLong() ?: projectId,
                    title = data.title ?: "Definition of Done",
                    checklist = data.checklist ?: emptyList(),
                    isActive = data.isActive ?: true,
                    createdByUserId = data.createdByUserId?.toLong() ?: 0L,
                    createdAt = data.createdAt?.toString(),
                    updatedAt = data.updatedAt?.toString()
                )
            }
        } else {
            throw Exception("Failed to update Definition of Done: ${response.message()}")
        }
        return null
    }
}

