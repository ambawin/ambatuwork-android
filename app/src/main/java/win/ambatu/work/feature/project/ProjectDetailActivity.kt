package win.ambatu.work.feature.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import win.ambatu.work.ui.theme.AmbatuWorkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectDetailActivity : ComponentActivity() {

    private val viewModel: ProjectDetailViewModel by viewModels()

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

    private val sprintBoardLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.loadProject()
        viewModel.loadBacklogItems()
        viewModel.loadSprints()
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
                        sprintBoardLauncher.launch(SprintBoardActivity.createIntent(this, projectId, sprintId))
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
