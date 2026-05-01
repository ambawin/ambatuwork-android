package win.ambatu.work.feature.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectDto(
    val id: Long,
    val name: String,
    val description: String?,
    @Json(name = "product_goal")
    val productGoal: String?,
    @Json(name = "owner_user_id")
    val ownerUserId: Long?,
    @Json(name = "default_sprint_length_days")
    val defaultSprintLengthDays: Int?,
    @Json(name = "wip_limit_per_member")
    val wipLimitPerMember: Int?,
    val status: String?,
    @Json(name = "my_role")
    val myRole: String?,
    @Json(name = "member_count")
    val memberCount: Int?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class ProjectListResponse(
    val data: List<ProjectDto>
)

@JsonClass(generateAdapter = true)
data class ProjectResponse(
    val data: ProjectDto
)

@JsonClass(generateAdapter = true)
data class CreateProjectRequest(
    val name: String,
    val description: String?,
    @Json(name = "product_goal")
    val productGoal: String,
    @Json(name = "default_sprint_length_days")
    val defaultSprintLengthDays: Int,
    @Json(name = "wip_limit_per_member")
    val wipLimitPerMember: Int? = null
)

@JsonClass(generateAdapter = true)
data class UpdateProjectRequest(
    val name: String? = null,
    val description: String? = null,
    @Json(name = "product_goal")
    val productGoal: String? = null,
    @Json(name = "default_sprint_length_days")
    val defaultSprintLengthDays: Int? = null,
    @Json(name = "wip_limit_per_member")
    val wipLimitPerMember: Int? = null,
    val status: String? = null
)

@JsonClass(generateAdapter = true)
data class ProjectMemberDto(
    val id: Long,
    @Json(name = "project_id")
    val projectId: Long,
    val role: String,
    val status: String,
    @Json(name = "joined_at")
    val joinedAt: String?,
    val user: UserDto
)

@JsonClass(generateAdapter = true)
data class ProjectMemberResponse(
    val data: ProjectMemberDto
)

@JsonClass(generateAdapter = true)
data class ProjectMemberListResponse(
    val data: List<ProjectMemberDto>
)

@JsonClass(generateAdapter = true)
data class UpdateMemberRoleRequest(
    val role: String
)

@JsonClass(generateAdapter = true)
data class ProjectInvitationDto(
    val id: Long,
    @Json(name = "project_id")
    val projectId: Long,
    val email: String,
    val role: String,
    val status: String,
    @Json(name = "expires_at")
    val expiresAt: String?,
    @Json(name = "accepted_at")
    val acceptedAt: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    val token: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateInvitationRequest(
    val email: String,
    val role: String
)

@JsonClass(generateAdapter = true)
data class InvitationResponse(
    val data: ProjectInvitationDto
)

@JsonClass(generateAdapter = true)
data class AcceptInvitationResponse(
    val message: String,
    val data: ProjectDto
)

@JsonClass(generateAdapter = true)
data class MessageResponse(
    val message: String
)
