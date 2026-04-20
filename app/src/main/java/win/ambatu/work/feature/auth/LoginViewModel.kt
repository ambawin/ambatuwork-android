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
    val isCheckingSession: Boolean = true,
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
        if (token == null) {
            _uiState.value = LoginUiState(isCheckingSession = false, isLoggedIn = false)
            return
        }

        viewModelScope.launch {
            try {
                val userDto = authRepository.getMe("Bearer $token")
                _uiState.value = LoginUiState(
                    isCheckingSession = false,
                    isLoggedIn = true,
                    user = userDto
                )
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 401) {
                    sessionManager.clearToken()
                }
                _uiState.value = LoginUiState(
                    isCheckingSession = false,
                    isLoggedIn = false,
                    error = if (e.code() == 401) null else "Server error: ${e.code()}"
                )
            } catch (e: java.io.IOException) {
                Log.e("Auth", "Network error connecting to ${BuildConfig.BASE_URL}", e)
                _uiState.value = LoginUiState(
                    isCheckingSession = false,
                    isLoggedIn = false,
                    error = "Network error: ${e.message}. Check your connection and BASE_URL."
                )
            } catch (e: Exception) {
                Log.e("Auth", "Unexpected error", e)
                _uiState.value = LoginUiState(
                    isCheckingSession = false,
                    isLoggedIn = false,
                    error = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearToken()
        _uiState.value = LoginUiState(isCheckingSession = false, isLoggedIn = false)
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isCheckingSession = false, error = null)

            try {
                val googleIdToken = googleSignInManager.getGoogleIdToken()
                Log.d("GoogleAuth", "Got token length=${googleIdToken.length}")

                val response = authRepository.loginWithGoogle(googleIdToken)
                Log.d("GoogleAuth", "Backend login success, token type=${response.tokenType}")

                Log.d("Network", "BASE_URL=${BuildConfig.BASE_URL}")


                sessionManager.saveToken(response.token)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    user = response.user
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Sign in failed"
                )
            }
        }
    }
}