package win.ambatu.work.data.repository

import win.ambatu.work.feature.network.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val apiService: ApiService
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
}
