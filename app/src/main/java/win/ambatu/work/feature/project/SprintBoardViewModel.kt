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
import win.ambatu.work.feature.network.SprintBoardDto
import win.ambatu.work.feature.network.UpdateBacklogItemRequest
import win.ambatu.work.feature.network.ProjectDto
import win.ambatu.work.feature.network.ProjectMemberDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class SprintBoardUiState(
    val board: SprintBoardDto? = null,
    val project: ProjectDto? = null,
    val members: List<ProjectMemberDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: Long? = null,
    val currentUserRole: String? = null
)

@HiltViewModel
class SprintBoardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val projectId = savedStateHandle.get<Long>("extra_project_id") ?: -1L
    private val sprintId = savedStateHandle.get<Long>("extra_sprint_id") ?: -1L

    private val _uiState = MutableStateFlow(SprintBoardUiState())
    val uiState: StateFlow<SprintBoardUiState> = _uiState.asStateFlow()

    init {
        loadBoard()
    }

    fun loadBoard() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Fetch basic info first
                val board = projectRepository.getSprintBoard(token, projectId, sprintId)
                
                // Fetch user, project and members info
                val user = authRepository.getMe("Bearer $token")
                val project = projectRepository.getProject(token, projectId)
                val members = projectRepository.getProjectMembers(token, projectId)
                
                _uiState.update { it.copy(
                    board = board, 
                    project = project,
                    members = members,
                    currentUserId = user.id,
                    currentUserRole = project.myRole,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateBacklogItemStatus(backlogId: Long, newStatus: String) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            try {
                projectRepository.updateBacklogItem(
                    token,
                    projectId,
                    backlogId,
                    UpdateBacklogItemRequest(status = newStatus)
                )
                loadBoard()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun archiveBacklogItem(backlogId: Long) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                projectRepository.archiveBacklogItem(token, projectId, backlogId)
                loadBoard()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateBacklogItem(
        backlogId: Long,
        title: String,
        description: String?,
        type: String,
        estimatePoints: Int?,
        businessValue: Int?,
        acceptanceCriteria: List<String>?,
        assignedToUserId: Long?
    ) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                projectRepository.updateBacklogItem(
                    token = token,
                    projectId = projectId,
                    backlogId = backlogId,
                    request = UpdateBacklogItemRequest(
                        title = title,
                        description = description,
                        type = type,
                        estimatePoints = estimatePoints,
                        businessValue = businessValue,
                        acceptanceCriteria = acceptanceCriteria,
                        assignedToUserId = assignedToUserId
                    )
                )
                loadBoard()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
