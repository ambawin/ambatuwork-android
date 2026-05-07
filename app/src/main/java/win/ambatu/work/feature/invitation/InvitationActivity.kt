package win.ambatu.work.feature.invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class InvitationActivity : ComponentActivity() {
    private val projectRepository by lazy { ProjectRepository(NetworkModule.apiService) }
    private val sessionManager by lazy { SessionManager(this) }

    private val viewModel: InvitationViewModel by viewModels {
        val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return InvitationViewModel(projectRepository, sessionManager) as T
            }
        }
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AmbatuWorkTheme {
                InvitationScreen(
                    viewModel = viewModel,
                    onInvitationAccepted = {
                        setResult(RESULT_OK)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, InvitationActivity::class.java)
        }
    }
}
