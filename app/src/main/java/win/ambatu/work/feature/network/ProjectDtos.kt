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
    @Json(name = "definition_of_done")
    val definitionOfDone: DefinitionOfDoneDto?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class DefinitionOfDoneDto(
    val id: Long,
    @Json(name = "project_id")
    val projectId: Long,
    val title: String,
    val checklist: List<String>,
    @Json(name = "is_active")
    val isActive: Boolean,
    @Json(name = "created_by_user_id")
    val createdByUserId: Long,
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
data class BacklogItemDto(
    val id: Long,
    @Json(name = "project_id")
    val projectId: Long,
    val title: String,
    val description: String?,
    val type: String,
    val status: String,
    val priority: String,
    @Json(name = "estimate_points")
    val estimatePoints: Int?,
    @Json(name = "acceptance_criteria")
    val acceptanceCriteria: List<String>?,
    @Json(name = "created_by_user_id")
    val createdByUserId: Long,
    @Json(name = "assigned_to_user_id")
    val assignedToUserId: Long?,
    @Json(name = "assigned_to_user")
    val assignedToUser: UserDto?,
    @Json(name = "done_at")
    val doneAt: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class BacklogItemListResponse(
    val data: List<BacklogItemDto>
)

@JsonClass(generateAdapter = true)
data class BacklogItemResponse(
    val data: BacklogItemDto
)

@JsonClass(generateAdapter = true)
data class CreateBacklogItemRequest(
    val title: String,
    val description: String? = null,
    val type: String? = "story",
    val priority: String? = "medium",
    @Json(name = "estimate_points")
    val estimatePoints: Int? = null,
    @Json(name = "acceptance_criteria")
    val acceptanceCriteria: List<String>? = null,
    @Json(name = "assigned_to_user_id")
    val assignedToUserId: Long? = null
)

@JsonClass(generateAdapter = true)
data class UpdateBacklogItemRequest(
    val title: String? = null,
    val description: String? = null,
    val type: String? = null,
    val priority: String? = null,
    @Json(name = "estimate_points")
    val estimatePoints: Int? = null,
    @Json(name = "acceptance_criteria")
    val acceptanceCriteria: List<String>? = null,
    @Json(name = "assigned_to_user_id")
    val assignedToUserId: Long? = null,
    val status: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateBacklogItemStatusRequest(
    val status: String
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
data class SprintDto(
    val id: Long,
    @Json(name = "project_id")
    val projectId: Long,
    val name: String,
    @Json(name = "sprint_goal")
    val sprintGoal: String?,
    val status: String,
    @Json(name = "start_date")
    val startDate: String?,
    @Json(name = "end_date")
    val endDate: String?,
    @Json(name = "created_by_user_id")
    val createdByUserId: Long,
    @Json(name = "closed_by_user_id")
    val closedByUserId: Long?,
    @Json(name = "closed_at")
    val closedAt: String?,
    @Json(name = "item_count")
    val itemCount: Int?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class SprintListResponse(
    val data: List<SprintDto>
)

@JsonClass(generateAdapter = true)
data class SprintResponse(
    val data: SprintDto
)

@JsonClass(generateAdapter = true)
data class CreateSprintRequest(
    val name: String,
    @Json(name = "sprint_goal")
    val sprintGoal: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String,
    @Json(name = "backlog_item_ids")
    val backlogItemIds: List<Long>
)

@JsonClass(generateAdapter = true)
data class SprintBoardDto(
    val sprint: SprintDto,
    val columns: SprintBoardColumnsDto
)

@JsonClass(generateAdapter = true)
data class SprintBoardColumnsDto(
    @Json(name = "selected")
    val selected: List<BacklogItemDto> = emptyList(),
    @Json(name = "in_progress")
    val inProgress: List<BacklogItemDto> = emptyList(),
    @Json(name = "in_review")
    val inReview: List<BacklogItemDto> = emptyList(),
    @Json(name = "done")
    val done: List<BacklogItemDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SprintBoardResponse(
    val data: SprintBoardDto
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
    val project: InvitationProjectDto? = null,
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
data class InvitationProjectDto(
    val id: Long,
    val name: String,
    val description: String?,
    val owner: UserDto? = null
)

@JsonClass(generateAdapter = true)
data class InvitationListResponse(
    val data: List<ProjectInvitationDto>
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

@JsonClass(generateAdapter = true)
data class CloseSprintResponse(
    val message: String,
    val data: SprintDto
)

@JsonClass(generateAdapter = true)
data class SprintReviewItemDto(
    val id: Long,
    @Json(name = "backlog_item_id")
    val backlogItemId: Long,
    val decision: String,
    val notes: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class SprintReviewDto(
    val id: Long,
    @Json(name = "sprint_id")
    val sprintId: Long,
    val summary: String,
    @Json(name = "demo_url")
    val demoUrl: String?,
    @Json(name = "created_by_user_id")
    val createdByUserId: Long,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    val items: List<SprintReviewItemDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SprintReviewResponse(
    val data: SprintReviewDto
)

@JsonClass(generateAdapter = true)
data class SprintReviewItemRequest(
    @Json(name = "backlog_item_id")
    val backlogItemId: Long,
    val decision: String,
    val notes: String? = null
)

@JsonClass(generateAdapter = true)
data class SubmitSprintReviewRequest(
    val summary: String,
    @Json(name = "demo_url")
    val demoUrl: String? = null,
    val items: List<SprintReviewItemRequest>
)

@JsonClass(generateAdapter = true)
data class DailyCheckinDto(
    val id: Long,
    @Json(name = "project_id")
    val projectId: Long,
    @Json(name = "sprint_id")
    val sprintId: Long,
    @Json(name = "user_id")
    val userId: Long,
    val yesterday: String?,
    val today: String?,
    val blockers: String?,
    @Json(name = "confidence_score")
    val confidenceScore: Int,
    @Json(name = "checkin_date")
    val checkinDate: String,
    val user: UserDto,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class DailyCheckinListResponse(
    val data: List<DailyCheckinDto>
)

@JsonClass(generateAdapter = true)
data class DailyCheckinResponse(
    val data: DailyCheckinDto
)

@JsonClass(generateAdapter = true)
data class SubmitDailyCheckinRequest(
    val yesterday: String? = null,
    val today: String? = null,
    val blockers: String? = null,
    @Json(name = "confidence_score")
    val confidenceScore: Int,
    @Json(name = "checkin_date")
    val checkinDate: String
)
