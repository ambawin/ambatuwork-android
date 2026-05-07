package win.ambatu.work.feature.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

data class SprintBoardUiState(
    val board: SprintBoardDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: Long? = null,
    val currentUserRole: String? = null
)

class SprintBoardViewModel(
    private val projectId: Long,
    private val sprintId: Long,
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    class Factory(
        private val projectId: Long,
        private val sprintId: Long,
        private val projectRepository: ProjectRepository,
        private val authRepository: AuthRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SprintBoardViewModel(projectId, sprintId, projectRepository, authRepository, sessionManager) as T
        }
    }

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
                
                // Fetch user and project info to determine permissions
                val user = authRepository.getMe("Bearer $token")
                val project = projectRepository.getProject(token, projectId)
                
                _uiState.update { it.copy(
                    board = board, 
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
}
