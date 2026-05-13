package win.ambatu.work.feature.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.NetworkModule
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class ProjectDetailActivity : ComponentActivity() {
    private val projectRepository by lazy { ProjectRepository(NetworkModule.apiService) }
    private val sessionManager by lazy { SessionManager(this) }
    
    private val viewModel: ProjectDetailViewModel by viewModels {
        val projectId = intent.getLongExtra(EXTRA_PROJECT_ID, -1L)
        ProjectDetailViewModel.Factory(projectId, projectRepository, sessionManager)
    }

    private val addBacklogLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.loadBacklogItems()
        }
    }

    private val addSprintLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.loadSprints()
        }
    }

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
                ProjectDetailScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onAddBacklogClick = { id ->
                        addBacklogLauncher.launch(AddBacklogItemActivity.createIntent(this, id))
                    },
                    onAddSprintClick = { id ->
                        addSprintLauncher.launch(AddSprintActivity.createIntent(this, id))
                    },
                    onSprintClick = { projectId, sprintId ->
                        startActivity(SprintBoardActivity.createIntent(this, projectId, sprintId))
                    }
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
