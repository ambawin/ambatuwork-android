package win.ambatu.work.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isStatsLoading: Boolean = false,
    val error: String? = null,
    val statsError: String? = null,
    val isLogoutSuccess: Boolean = false,
    val stats: win.ambatu.work.feature.network.UserStatsDto? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserStats()
    }

    fun fetchUserStats() {
        val token = sessionManager.getToken()
        if (token == null) {
            _uiState.update { it.copy(statsError = "No authorization token found") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isStatsLoading = true, statsError = null) }
            try {
                val statsDto = authRepository.getUserStats(token)
                _uiState.update { it.copy(isStatsLoading = false, stats = statsDto) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isStatsLoading = false, statsError = e.message ?: "Failed to fetch stats") }
            }
        }
    }

    fun logout() {
        val token = sessionManager.getToken()
        if (token == null) {
            sessionManager.clearToken()
            _uiState.update { it.copy(isLogoutSuccess = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.logout(token)
                sessionManager.clearToken()
                _uiState.update { it.copy(isLoading = false, isLogoutSuccess = true) }
            } catch (e: Exception) {
                // Fallback: clear session even if server request fails so user doesn't get stuck
                sessionManager.clearToken()
                _uiState.update { it.copy(isLoading = false, isLogoutSuccess = true, error = e.message) }
            }
        }
    }
}
