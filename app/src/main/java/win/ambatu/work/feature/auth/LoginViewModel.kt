package win.ambatu.work.feature.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import win.ambatu.work.feature.auth.GoogleSignInManager
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import win.ambatu.work.BuildConfig

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val user: win.ambatu.work.feature.network.UserDto? = null
)

class LoginViewModel(
    private val googleSignInManager: GoogleSignInManager,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        checkSession()
    }

    private fun checkSession() {
        val token = sessionManager.getToken()
        if (token != null) {
            viewModelScope.launch {
                _uiState.value = LoginUiState(isLoading = true)
                try {
                    val userDto = authRepository.getMe("Bearer $token")
                    _uiState.value = LoginUiState(
                        isLoggedIn = true,
                        user = userDto
                    )
                } catch (e: Exception) {
                    Log.e("Auth", "Failed to fetch user with saved token", e)
                    sessionManager.clearToken()
                    _uiState.value = LoginUiState(isLoggedIn = false)
                }
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            try {
                val googleIdToken = googleSignInManager.getGoogleIdToken()
                Log.d("GoogleAuth", "Got token length=${googleIdToken.length}")

                val response = authRepository.loginWithGoogle(googleIdToken)
                Log.d("GoogleAuth", "Backend login success, token type=${response.tokenType}")

                Log.d("Network", "BASE_URL=${BuildConfig.BASE_URL}")


                sessionManager.saveToken(response.token)

                _uiState.value = LoginUiState(
                    isLoading = false,
                    isLoggedIn = true,
                    user = response.user
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    error = e.message ?: "Sign in failed"
                )
            }
        }
    }
}