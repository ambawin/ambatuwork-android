package win.ambatu.work.data.repository

import win.ambatu.work.feature.network.*

class ProjectRepository(
    private val apiService: ApiService
) {
    private fun getAuthHeader(token: String) = "Bearer $token"

    suspend fun getProjects(token: String): List<ProjectDto> {
        return apiService.getProjects(getAuthHeader(token)).data
    }

    suspend fun createProject(token: String, request: CreateProjectRequest): ProjectDto {
        return apiService.createProject(getAuthHeader(token), request)
    }

    suspend fun getProject(token: String, projectId: Long): ProjectDto {
        return apiService.getProject(getAuthHeader(token), projectId)
    }

    suspend fun updateProject(token: String, projectId: Long, request: UpdateProjectRequest): ProjectDto {
        return apiService.updateProject(getAuthHeader(token), projectId, request)
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
        )
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

    suspend fun acceptInvitation(token: String, invitationToken: String): ProjectDto {
        val response = apiService.acceptInvitation(getAuthHeader(token), invitationToken)
        return response.data
    }
}
