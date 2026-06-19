package win.ambatu.work.feature.project

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.SprintBoardDto
import win.ambatu.work.feature.network.SprintReviewItemRequest
import win.ambatu.work.feature.network.SubmitSprintReviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class CloseSprintUiState(
    val board: SprintBoardDto? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isFinished: Boolean = false
)

@HiltViewModel
class CloseSprintViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val projectId = savedStateHandle.get<Long>("extra_project_id") ?: -1L
    private val sprintId = savedStateHandle.get<Long>("extra_sprint_id") ?: -1L

    private val _uiState = MutableStateFlow(CloseSprintUiState())
    val uiState: StateFlow<CloseSprintUiState> = _uiState.asStateFlow()

    init {
        loadSprintBoard()
    }

    fun loadSprintBoard() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val board = projectRepository.getSprintBoard(token, projectId, sprintId)
                _uiState.update { it.copy(board = board, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load sprint board") }
            }
        }
    }

    fun closeSprint(
        summary: String,
        demoUrl: String?,
        items: List<SprintReviewItemRequest>
    ) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            try {
                if (items.isNotEmpty()) {
                    projectRepository.submitSprintReview(
                        token = token,
                        projectId = projectId,
                        sprintId = sprintId,
                        request = SubmitSprintReviewRequest(
                            summary = summary,
                            demoUrl = demoUrl,
                            items = items
                        )
                    )
                }
                projectRepository.closeSprint(token, projectId, sprintId)
                _uiState.update { it.copy(isSubmitting = false, isFinished = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, error = e.message ?: "Failed to close sprint") }
            }
        }
    }
}
