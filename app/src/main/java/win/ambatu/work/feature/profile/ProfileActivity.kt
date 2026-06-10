package win.ambatu.work.feature.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import win.ambatu.work.data.model.User
import win.ambatu.work.feature.auth.LoginActivity
import win.ambatu.work.feature.scrum.ScrumGuideActivity
import win.ambatu.work.ui.theme.AmbatuWorkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = intent.getParcelableExtra<User>(EXTRA_USER) ?: return finish()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            if (uiState.isLogoutSuccess) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            AmbatuWorkTheme {
                ProfileScreen(
                    user = user,
                    isLoading = uiState.isLoading,
                    onLogoutClick = { viewModel.logout() },
                    onBackClick = { finish() },
                    onScrumGuideClick = {
                        startActivity(ScrumGuideActivity.createIntent(this))
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_USER = "extra_user"

        fun createIntent(context: Context, user: User): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra(EXTRA_USER, user)
            }
        }
    }
}
