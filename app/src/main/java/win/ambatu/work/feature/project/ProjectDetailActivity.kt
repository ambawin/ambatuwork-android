package win.ambatu.work.feature.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class ProjectDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val projectId = intent.getLongExtra(EXTRA_PROJECT_ID, -1L)
        if (projectId == -1L) {
            finish()
            return
        }

        setContent {
            AmbatuWorkTheme {
                val context = this
                val sessionManager = remember { SessionManager(context) }
                val projectRepository = remember { ProjectRepository(NetworkModule.apiService) }
                
                val projectDetailViewModel: ProjectDetailViewModel = viewModel {
                    ProjectDetailViewModel(
                        projectId = projectId,
                        projectRepository = projectRepository,
                        sessionManager = sessionManager
                    )
                }
                
                ProjectDetailScreen(
                    viewModel = projectDetailViewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_PROJECT_ID = "extra_project_id"

        fun createIntent(context: Context, projectId: Long): Intent {
            return Intent(context, ProjectDetailActivity::class.java).apply {
                putExtra(EXTRA_PROJECT_ID, projectId)
            }
        }
    }
}
