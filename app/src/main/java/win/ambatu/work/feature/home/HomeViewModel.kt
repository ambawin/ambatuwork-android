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
import win.ambatu.work.data.model.User
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.feature.network.CreateProjectRequest
import win.ambatu.work.feature.network.ProjectDto

data class HomeUiState(
    val projects: List<ProjectDto> = emptyList(),
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreatingProject: Boolean = false,
    val isInvitingUser: Boolean = false
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val projectRepository: ProjectRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUser()
        loadProjects()
    }

    fun loadUser() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            try {
                val dto = authRepository.getMe("Bearer $token")
                val user = User(
                    id = dto.id?.toInt() ?: 0,
                    name = dto.name ?: "",
                    email = dto.email ?: "",
                    picture = dto.avatarUrl,
                    points = 0,
                    rank = 0
                )
                _uiState.update { it.copy(user = user) }
            } catch (e: Exception) {
                // Handle error
            }
        }
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

    fun createProject(name: String, description: String, productGoal: String, defaultSprintLength: Int) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingProject = true, error = null) }
            try {
                val request = CreateProjectRequest(
                    name = name,
                    description = description,
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

    fun inviteUser(projectId: Long, email: String) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isInvitingUser = true, error = null) }
            try {
                projectRepository.createInvitation(token, projectId, email, "member")
                _uiState.update { it.copy(isInvitingUser = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isInvitingUser = false, error = e.message) }
            }
        }
    }
}
