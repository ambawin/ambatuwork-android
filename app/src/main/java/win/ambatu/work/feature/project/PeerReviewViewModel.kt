package win.ambatu.work.feature.project

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.PeerReviewCycleDto
import win.ambatu.work.feature.network.PeerReviewSummaryItemDto
import win.ambatu.work.feature.network.ProjectMemberDto
import win.ambatu.work.feature.network.SubmitPeerReviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class PeerReviewUiState(
    val cycle: PeerReviewCycleDto? = null,
    val members: List<ProjectMemberDto> = emptyList(),
    val summary: List<PeerReviewSummaryItemDto> = emptyList(),
    val mySummary: PeerReviewSummaryItemDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val currentUserId: Long? = null,
    val currentUserRole: String? = null,
    val showReviewForm: Boolean = false,
    val selectedRevieweeId: Long? = null
)

@HiltViewModel
class PeerReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val projectId = savedStateHandle.get<Long>("extra_project_id") ?: -1L
    private val sprintId = savedStateHandle.get<Long>("extra_sprint_id") ?: -1L

    private val _uiState = MutableStateFlow(PeerReviewUiState())
    val uiState: StateFlow<PeerReviewUiState> = _uiState.asStateFlow()

    init {
        loadCycle()
    }

    fun loadCycle() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = authRepository.getMe("Bearer $token")
                val project = projectRepository.getProject(token, projectId)
                val members = projectRepository.getProjectMembers(token, projectId)

                val cycle = try {
                    projectRepository.getPeerReviewCycle(token, projectId, sprintId)
                } catch (_: Exception) {
                    null
                }

                // Load summary if cycle exists and is closed
                var summaryList = emptyList<PeerReviewSummaryItemDto>()
                var mySummary: PeerReviewSummaryItemDto? = null

                if (cycle != null) {
                    try {
                        summaryList = projectRepository.getPeerReviewCycleSummary(
                            token, projectId, cycle.id
                        )
                    } catch (_: Exception) {}

                    try {
                        mySummary = projectRepository.getMyPeerReviewSummary(
                            token, projectId, cycle.id
                        )
                    } catch (_: Exception) {}
                }

                _uiState.update {
                    it.copy(
                        cycle = cycle,
                        members = members,
                        summary = summaryList,
                        mySummary = mySummary,
                        currentUserId = user.id,
                        currentUserRole = project.myRole,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun openCycle() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val cycle = projectRepository.openPeerReviewCycle(token, projectId, sprintId)
                _uiState.update {
                    it.copy(cycle = cycle, isLoading = false, successMessage = "Peer review cycle opened!")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun closeCycle() {
        val cycle = _uiState.value.cycle ?: return
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val updatedCycle = projectRepository.closePeerReviewCycle(token, projectId, cycle.id)
                // Reload summaries after closing
                val summaryList = try {
                    projectRepository.getPeerReviewCycleSummary(token, projectId, cycle.id)
                } catch (_: Exception) { emptyList() }
                val mySummary = try {
                    projectRepository.getMyPeerReviewSummary(token, projectId, cycle.id)
                } catch (_: Exception) { null }

                _uiState.update {
                    it.copy(
                        cycle = updatedCycle,
                        summary = summaryList,
                        mySummary = mySummary,
                        isLoading = false,
                        successMessage = "Cycle closed! Results are now visible."
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun showReviewFormFor(revieweeId: Long) {
        _uiState.update { it.copy(showReviewForm = true, selectedRevieweeId = revieweeId) }
    }

    fun hideReviewForm() {
        _uiState.update { it.copy(showReviewForm = false, selectedRevieweeId = null) }
    }

    fun submitReview(
        revieweeUserId: Long,
        collaborationScore: Int,
        deliveryScore: Int,
        communicationScore: Int,
        continueFeedback: String?,
        improveFeedback: String?
    ) {
        val cycle = _uiState.value.cycle ?: return
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                projectRepository.submitPeerReview(
                    token, projectId, cycle.id,
                    SubmitPeerReviewRequest(
                        revieweeUserId = revieweeUserId,
                        collaborationScore = collaborationScore,
                        deliveryScore = deliveryScore,
                        communicationScore = communicationScore,
                        continueFeedback = continueFeedback?.takeIf { it.isNotBlank() },
                        improveFeedback = improveFeedback?.takeIf { it.isNotBlank() }
                    )
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showReviewForm = false,
                        selectedRevieweeId = null,
                        successMessage = "Review submitted!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun isAdmin(): Boolean {
        val role = _uiState.value.currentUserRole?.lowercase()?.trim() ?: ""
        return role.contains("admin") ||
               role.contains("owner") ||
               role.contains("master") ||
               role.contains("leader")
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
