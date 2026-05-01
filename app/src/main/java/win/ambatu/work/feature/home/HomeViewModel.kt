package win.ambatu.work.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.CreateProjectRequest
import win.ambatu.work.feature.network.ProjectDto

data class HomeUiState(
    val projects: List<ProjectDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreatingProject: Boolean = false,
    val isAcceptingInvitation: Boolean = false
)

class HomeViewModel(
    private val projectRepository: ProjectRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = projectRepository.getProjects(token)
                _uiState.update { it.copy(projects = response, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createProject(name: String, productGoal: String, defaultSprintLength: Int) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingProject = true, error = null) }
            try {
                val request = CreateProjectRequest(
                    name = name,
                    description = "", // Or pass from UI
                    productGoal = productGoal,
                    defaultSprintLengthDays = defaultSprintLength
                )
                projectRepository.createProject(token, request)
                loadProjects() // Refresh list
                _uiState.update { it.copy(isCreatingProject = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCreatingProject = false, error = e.message) }
            }
        }
    }

    fun acceptInvitation(invitationToken: String) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isAcceptingInvitation = true, error = null) }
            try {
                projectRepository.acceptInvitation(token, invitationToken)
                loadProjects() // Refresh list
                _uiState.update { it.copy(isAcceptingInvitation = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAcceptingInvitation = false, error = e.message) }
            }
        }
    }
}
