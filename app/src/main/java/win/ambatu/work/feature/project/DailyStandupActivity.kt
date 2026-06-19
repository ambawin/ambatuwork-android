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
class DailyStandupActivity : ComponentActivity() {

    private val viewModel: DailyStandupViewModel by viewModels()

    private val addCheckinLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.loadCheckins()
        }
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
                DailyStandupScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onCheckinClick = {
                        addCheckinLauncher.launch(
                            AddDailyCheckinActivity.createIntent(this, projectId, sprintId)
                        )
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_PROJECT_ID = "extra_project_id"
        private const val EXTRA_SPRINT_ID = "extra_sprint_id"

        fun createIntent(context: Context, projectId: Long, sprintId: Long): Intent {
            return Intent(context, DailyStandupActivity::class.java).apply {
                putExtra(EXTRA_PROJECT_ID, projectId)
                putExtra(EXTRA_SPRINT_ID, sprintId)
            }
        }
    }
}
