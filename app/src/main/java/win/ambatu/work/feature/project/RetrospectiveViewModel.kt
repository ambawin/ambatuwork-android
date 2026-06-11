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
import win.ambatu.work.feature.network.RetrospectiveDto
import win.ambatu.work.feature.network.CreateRetroItemRequest
import win.ambatu.work.feature.network.ProjectMemberDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class RetrospectiveUiState(
    val retrospective: RetrospectiveDto? = null,
    val members: List<ProjectMemberDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val currentUserId: Long? = null,
    val selectedTab: Int = 0  // 0=went_well, 1=problem, 2=action
)

@HiltViewModel
class RetrospectiveViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val projectId = savedStateHandle.get<Long>("extra_project_id") ?: -1L
    private val sprintId = savedStateHandle.get<Long>("extra_sprint_id") ?: -1L

    private val _uiState = MutableStateFlow(RetrospectiveUiState())
    val uiState: StateFlow<RetrospectiveUiState> = _uiState.asStateFlow()

    init {
        loadRetrospective()
    }

    fun loadRetrospective() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = authRepository.getMe("Bearer $token")
                val members = projectRepository.getProjectMembers(token, projectId)

                val retro = try {
                    projectRepository.getRetrospective(token, projectId, sprintId)
                } catch (_: Exception) {
                    null // 404 means no retro exists yet
                }

                _uiState.update {
                    it.copy(
                        retrospective = retro,
                        members = members,
                        currentUserId = user.id,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun submitHappinessScore(score: Int) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val retro = projectRepository.submitHappinessScore(token, projectId, sprintId, score)
                _uiState.update {
                    it.copy(
                        retrospective = retro,
                        isLoading = false,
                        successMessage = "Happiness score submitted!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun addFeedbackItem(type: String, body: String, assignedToUserId: Long? = null) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                projectRepository.createRetroItem(
                    token, projectId, sprintId,
                    CreateRetroItemRequest(
                        type = type,
                        body = body,
                        assignedToUserId = assignedToUserId
                    )
                )
                // Reload full retrospective to refresh the list
                val retro = projectRepository.getRetrospective(token, projectId, sprintId)
                _uiState.update {
                    it.copy(
                        retrospective = retro,
                        isLoading = false,
                        successMessage = "Feedback added!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteFeedbackItem(itemId: Long) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                projectRepository.deleteRetroItem(token, projectId, sprintId, itemId)
                val retro = projectRepository.getRetrospective(token, projectId, sprintId)
                _uiState.update {
                    it.copy(
                        retrospective = retro,
                        isLoading = false,
                        successMessage = "Item deleted"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
