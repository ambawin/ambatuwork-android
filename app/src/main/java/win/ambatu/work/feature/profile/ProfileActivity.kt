package win.ambatu.work.feature.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import win.ambatu.work.data.model.User
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.auth.LoginActivity
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = intent.getParcelableExtra<User>(EXTRA_USER) ?: return finish()

        setContent {
            AmbatuWorkTheme {
                ProfileScreen(
                    user = user,
                    onLogoutClick = {
                        SessionManager(this).clearToken()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    },
                    onBackClick = { finish() }
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
