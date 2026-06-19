package win.ambatu.work.feature.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserStatsResponse(
    val data: UserStatsDto
)

@JsonClass(generateAdapter = true)
data class UserStatsDto(
    val projects: ProjectsStatsDto,
    @Json(name = "backlog_items")
    val backlogItems: BacklogItemsStatsDto,
    @Json(name = "daily_checkins")
    val dailyCheckins: DailyCheckinsStatsDto,
    val impediments: ImpedimentsStatsDto,
    @Json(name = "peer_reviews")
    val peerReviews: PeerReviewsStatsDto
)

@JsonClass(generateAdapter = true)
data class ProjectsStatsDto(
    @Json(name = "total_active")
    val totalActive: Int
)

@JsonClass(generateAdapter = true)
data class BacklogItemsStatsDto(
    @Json(name = "assigned_total")
    val assignedTotal: Int,
    @Json(name = "assigned_by_status")
    val assignedByStatus: BacklogStatusStatsDto,
    @Json(name = "completed_points")
    val completedPoints: Int
)

@JsonClass(generateAdapter = true)
data class BacklogStatusStatsDto(
    val backlog: Int,
    val ready: Int,
    val selected: Int,
    @Json(name = "in_progress")
    val inProgress: Int,
    @Json(name = "in_review")
    val inReview: Int,
    val done: Int
)

@JsonClass(generateAdapter = true)
data class DailyCheckinsStatsDto(
    @Json(name = "total_submitted")
    val totalSubmitted: Int,
    @Json(name = "average_confidence")
    val averageConfidence: Float?
)

@JsonClass(generateAdapter = true)
data class ImpedimentsStatsDto(
    @Json(name = "reported_total")
    val reportedTotal: Int,
    @Json(name = "reported_resolved")
    val reportedResolved: Int,
    @Json(name = "reported_by_status")
    val reportedByStatus: ImpedimentStatusStatsDto
)

@JsonClass(generateAdapter = true)
data class ImpedimentStatusStatsDto(
    val open: Int,
    @Json(name = "in_progress")
    val inProgress: Int,
    val resolved: Int,
    val ignored: Int
)

@JsonClass(generateAdapter = true)
data class PeerReviewsStatsDto(
    @Json(name = "submitted_total")
    val submittedTotal: Int,
    @Json(name = "received_total")
    val receivedTotal: Int,
    @Json(name = "received_average_scores")
    val receivedAverageScores: PeerReviewAverageScoresDto
)

@JsonClass(generateAdapter = true)
data class PeerReviewAverageScoresDto(
    val collaboration: Float?,
    val delivery: Float?,
    val communication: Float?
)
