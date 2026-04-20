package win.ambatu.work

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.auth.GoogleSignInManager
import win.ambatu.work.feature.auth.LoginScreen
import win.ambatu.work.feature.auth.LoginViewModel
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        val authRepository = AuthRepository(NetworkModule.apiService)
        val googleSignInManager = GoogleSignInManager(this)

        val loginViewModel = LoginViewModel(
            googleSignInManager = googleSignInManager,
            authRepository = authRepository,
            sessionManager = sessionManager
        )

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme {
                LoginScreen(
                    viewModel = loginViewModel
                )
            }
        }
    }
}