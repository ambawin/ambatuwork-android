package win.ambatu.work.feature.project

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

class SprintBoardActivity : ComponentActivity() {
    private val projectRepository by lazy { ProjectRepository(NetworkModule.apiService) }
    private val sessionManager by lazy { SessionManager(this) }
    
    private val viewModel: SprintBoardViewModel by viewModels {
        val projectId = intent.getLongExtra(EXTRA_PROJECT_ID, -1L)
        val sprintId = intent.getLongExtra(EXTRA_SPRINT_ID, -1L)
        SprintBoardViewModel.Factory(projectId, sprintId, projectRepository, sessionManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val projectId = intent.getLongExtra(EXTRA_PROJECT_ID, -1L)
        val sprintId = intent.getLongExtra(EXTRA_SPRINT_ID, -1L)
        if (projectId == -1L || sprintId == -1L) {
            finish()
            return
        }

        setContent {
            AmbatuWorkTheme {
                SprintBoardScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_PROJECT_ID = "extra_project_id"
        private const val EXTRA_SPRINT_ID = "extra_sprint_id"

        fun createIntent(context: Context, projectId: Long, sprintId: Long): Intent {
            return Intent(context, SprintBoardActivity::class.java).apply {
                putExtra(EXTRA_PROJECT_ID, projectId)
                putExtra(EXTRA_SPRINT_ID, sprintId)
            }
        }
    }
}
