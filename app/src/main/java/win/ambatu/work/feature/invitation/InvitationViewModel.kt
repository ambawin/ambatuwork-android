package win.ambatu.work.feature.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.ProjectInvitationDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class InvitationUiState(
    val invitations: List<ProjectInvitationDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAccepting: Boolean = false,
    val acceptSuccess: Boolean = false
)

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvitationUiState())
    val uiState: StateFlow<InvitationUiState> = _uiState.asStateFlow()

    init {
        loadInvitations()
    }

    fun loadInvitations() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = projectRepository.getInvitations(token)
                _uiState.update { it.copy(invitations = response, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun acceptInvitation(invitationToken: String) {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isAccepting = true, error = null, acceptSuccess = false) }
            try {
                projectRepository.acceptInvitation(token, invitationToken)
                _uiState.update { it.copy(isAccepting = false, acceptSuccess = true) }
                loadInvitations() // Refresh list
            } catch (e: Exception) {
                _uiState.update { it.copy(isAccepting = false, error = e.message) }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(acceptSuccess = false) }
    }
}
