package win.ambatu.work.feature.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectStatsResponse(
    val data: ProjectStatsDto
)

@JsonClass(generateAdapter = true)
data class ProjectStatsDto(
    val project: ProjectInfoStatsDto,
    val members: MembersStatsDto,
    val sprints: SprintsStatsDto,
    @Json(name = "backlog_items")
    val backlogItems: BacklogItemsProjectStatsDto,
    @Json(name = "daily_checkins")
    val dailyCheckins: DailyCheckinsProjectStatsDto,
    val impediments: ImpedimentsProjectStatsDto,
    val retrospectives: RetrospectivesStatsDto,
    @Json(name = "peer_reviews")
    val peerReviews: PeerReviewsProjectStatsDto
)

@JsonClass(generateAdapter = true)
data class ProjectInfoStatsDto(
    val id: Long,
    val name: String,
    val status: String
)

@JsonClass(generateAdapter = true)
data class MembersStatsDto(
    val total: Int,
    @Json(name = "by_role")
    val byRole: MembersByRoleStatsDto
)

@JsonClass(generateAdapter = true)
data class MembersByRoleStatsDto(
    val owner: Int = 0,
    val member: Int = 0,
    val supervisor: Int = 0
)

@JsonClass(generateAdapter = true)
data class SprintsStatsDto(
    val total: Int,
    val active: Int,
    val completed: Int,
    @Json(name = "average_velocity")
    val averageVelocity: Float?
)

@JsonClass(generateAdapter = true)
data class BacklogItemsProjectStatsDto(
    val total: Int,
    @Json(name = "total_points")
    val totalPoints: Int,
    @Json(name = "completed_points")
    val completedPoints: Int,
    @Json(name = "by_status")
    val byStatus: BacklogStatusStatsDto
)

@JsonClass(generateAdapter = true)
data class DailyCheckinsProjectStatsDto(
    @Json(name = "total_submitted")
    val totalSubmitted: Int,
    @Json(name = "average_confidence")
    val averageConfidence: Float?
)

@JsonClass(generateAdapter = true)
data class ImpedimentsProjectStatsDto(
    val total: Int,
    val resolved: Int,
    @Json(name = "by_status")
    val byStatus: ImpedimentStatusStatsDto
)

@JsonClass(generateAdapter = true)
data class RetrospectivesStatsDto(
    val total: Int,
    @Json(name = "average_happiness_score")
    val averageHappinessScore: Float?
)

@JsonClass(generateAdapter = true)
data class PeerReviewsProjectStatsDto(
    @Json(name = "total_cycles")
    val totalCycles: Int,
    @Json(name = "average_scores")
    val averageScores: PeerReviewAverageScoresDto
)
