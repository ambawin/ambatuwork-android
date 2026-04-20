package win.ambatu.work

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import win.ambatu.work.controller.DeveloperController
import win.ambatu.work.data.intent.MainIntents
import win.ambatu.work.data.model.User
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.auth.GoogleSignInManager
import win.ambatu.work.feature.auth.LoginViewModel
import win.ambatu.work.feature.profile.ProfileScreen
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class ProfileActivity : ComponentActivity() {
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

        val user: User = User(
            id = intent.getIntExtra(MainIntents.EXTRA_USER_ID, 1),
            name = intent.getStringExtra(MainIntents.EXTRA_USER_NAME) ?: "",
            email = intent.getStringExtra(MainIntents.EXTRA_USER_EMAIL) ?: "",
            picture = intent.getStringExtra(MainIntents.EXTRA_USER_PICTURE)
        )

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme {
                ProfileScreen(
                    user = user,
                    onNavigationBackClick = {
                        finish()
                    },
                    onProfileIconClick = {
                        startActivity(MainIntents.toLogin(this))
                    },
                    onAboutClick = {
                        startActivity(MainIntents.toAbout(this, DeveloperController.getAll()))
                    },
                    onLogoutClick = {
                        loginViewModel.logout()
                        finishAffinity()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                )
            }
        }
    }
}