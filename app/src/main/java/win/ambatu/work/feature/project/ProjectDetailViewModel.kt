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
import win.ambatu.work.feature.network.BacklogItemDto
import win.ambatu.work.feature.network.ProjectDto
import win.ambatu.work.feature.network.ProjectMemberDto
import win.ambatu.work.feature.network.SprintDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class ProjectDetailUiState(
    val project: ProjectDto? = null,
    val members: List<ProjectMemberDto> = emptyList(),
    val backlogItems: List<BacklogItemDto> = emptyList(),
    val sprints: List<SprintDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val projectId = savedStateHandle.get<Long>("extra_project_id") ?: -1L

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    init {
        loadProject()
        loadMembers()
        loadBacklogItems()
        loadSprints()
    }

    fun loadProject() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val project = projectRepository.getProject(token, projectId)
                _uiState.update { it.copy(project = project, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadMembers() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            try {
                val members = projectRepository.getProjectMembers(token, projectId)
                _uiState.update { it.copy(members = members) }
            } catch (e: Exception) {
                // Handle member loading error if needed
            }
        }
    }

    fun loadBacklogItems() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            try {
                val items = projectRepository.getProjectBacklogItems(token, projectId)
                _uiState.update { it.copy(backlogItems = items) }
            } catch (e: Exception) {
                // Handle backlog loading error if needed
            }
        }
    }

    fun loadSprints() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            try {
                val sprints = projectRepository.getProjectSprints(token, projectId)
                _uiState.update { it.copy(sprints = sprints) }
            } catch (e: Exception) {
                // Handle sprint loading error if needed
            }
        }
    }
}
