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
import win.ambatu.work.feature.network.BacklogItemDto
import win.ambatu.work.feature.network.ProjectDto
import win.ambatu.work.feature.network.ProjectMemberDto
import win.ambatu.work.feature.network.SprintDto
import win.ambatu.work.feature.network.UpdateProjectRequest
import win.ambatu.work.feature.network.UpdateBacklogItemRequest
import win.ambatu.work.feature.network.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class HomeUiState(
    val projects: List<ProjectDto> = emptyList(),
    val selectedProject: ProjectDto? = null,
    val members: List<ProjectMemberDto> = emptyList(),
    val backlogItems: List<BacklogItemDto> = emptyList(),
    val sprints: List<SprintDto> = emptyList(),
    val sprintAssignees: Map<Long, List<UserDto>> = emptyMap(),
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreatingProject: Boolean = false,
    val isInvitingUser: Boolean = false,
    val isStatsLoading: Boolean = false,
    val statsError: String? = null,
    val stats: win.ambatu.work.feature.network.ProjectStatsDto? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
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
                val firstProject = response.firstOrNull()
                val currentSelected = _uiState.value.selectedProject
                
                _uiState.update { 
                    it.copy(
                        projects = response, 
                        isLoading = false,
                        selectedProject = currentSelected ?: firstProject
                    ) 
                }

                val projectToLoad = currentSelected ?: firstProject
                if (projectToLoad != null) {
                    loadProjectDetails(projectToLoad.id)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectProject(project: ProjectDto) {
        _uiState.update { it.copy(selectedProject = project, stats = null, statsError = null) }
        loadProjectDetails(project.id)
    }

    fun loadProjectStats(projectId: Long) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isStatsLoading = true, statsError = null) }
            try {
                val stats = projectRepository.getProjectStats(token, projectId)
                _uiState.update { it.copy(stats = stats, isStatsLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isStatsLoading = false, statsError = e.message ?: "Failed to fetch project stats") }
            }
        }
    }

    private fun loadProjectDetails(projectId: Long) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            try {
                // Fetch details in parallel if possible, or sequentially for simplicity
                val members = projectRepository.getProjectMembers(token, projectId)
                val backlog = projectRepository.getProjectBacklogItems(token, projectId)
                val sprints = projectRepository.getProjectSprints(token, projectId)
                
                _uiState.update { 
                    it.copy(
                        members = members,
                        backlogItems = backlog,
                        sprints = sprints
                    )
                }

                // Also trigger stats load
                loadProjectStats(projectId)

                // Fetch sprint board details to retrieve assignee profiles
                val sprintAssigneesMap = mutableMapOf<Long, List<UserDto>>()
                sprints.forEach { sprint ->
                    try {
                        val board = projectRepository.getSprintBoard(token, projectId, sprint.id)
                        val allItems = board.columns.selected + board.columns.inProgress + board.columns.inReview + board.columns.done
                        val assignees = allItems.mapNotNull { it.assignedToUser }.distinctBy { it.id }
                        sprintAssigneesMap[sprint.id] = assignees
                    } catch (e: Exception) {
                        // Ignore individual board fetch failure
                    }
                }
                _uiState.update { it.copy(sprintAssignees = sprintAssigneesMap) }
            } catch (e: Exception) {
                // Handle detail loading error
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

    fun updateProject(
        name: String?,
        description: String?,
        productGoal: String?,
        sprintLength: Int?
    ) {
        val token = sessionManager.getToken() ?: return
        val projectId = _uiState.value.selectedProject?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val updated = projectRepository.updateProject(
                    token = token,
                    projectId = projectId,
                    request = UpdateProjectRequest(
                        name = name,
                        description = description,
                        productGoal = productGoal,
                        defaultSprintLengthDays = sprintLength
                    )
                )
                _uiState.update { it.copy(selectedProject = updated, isLoading = false) }
                loadProjects()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun archiveBacklogItem(backlogId: Long) {
        val token = sessionManager.getToken() ?: return
        val projectId = _uiState.value.selectedProject?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                projectRepository.archiveBacklogItem(token, projectId, backlogId)
                loadProjectDetails(projectId)
                _uiState.update { it.copy(isLoading = false) }
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
        priority: String?,
        acceptanceCriteria: List<String>?,
        assignedToUserId: Long?
    ) {
        val token = sessionManager.getToken() ?: return
        val projectId = _uiState.value.selectedProject?.id ?: return
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
                        priority = priority,
                        acceptanceCriteria = acceptanceCriteria,
                        assignedToUserId = assignedToUserId
                    )
                )
                loadProjectDetails(projectId)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateProjectMemberRole(userId: Long, role: String) {
        val token = sessionManager.getToken() ?: return
        val projectId = _uiState.value.selectedProject?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                projectRepository.updateProjectMemberRole(token, projectId, userId, role)
                loadProjectDetails(projectId)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun removeProjectMember(userId: Long) {
        val token = sessionManager.getToken() ?: return
        val projectId = _uiState.value.selectedProject?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                projectRepository.removeProjectMember(token, projectId, userId)
                loadProjectDetails(projectId)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
