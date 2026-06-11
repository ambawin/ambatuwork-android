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
import win.ambatu.work.feature.network.DailyCheckinDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class DailyStandupUiState(
    val checkins: List<DailyCheckinDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: Long? = null
)

@HiltViewModel
class DailyStandupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val projectId = savedStateHandle.get<Long>("extra_project_id") ?: -1L
    private val sprintId = savedStateHandle.get<Long>("extra_sprint_id") ?: -1L

    private val _uiState = MutableStateFlow(DailyStandupUiState())
    val uiState: StateFlow<DailyStandupUiState> = _uiState.asStateFlow()

    init {
        loadCheckins()
    }

    fun loadCheckins() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = authRepository.getMe("Bearer $token")
                val checkins = try {
                    projectRepository.getDailyCheckins(token, projectId, sprintId)
                } catch (e: Exception) {
                    emptyList()
                }
                _uiState.update {
                    it.copy(
                        checkins = checkins,
                        currentUserId = user.id,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
