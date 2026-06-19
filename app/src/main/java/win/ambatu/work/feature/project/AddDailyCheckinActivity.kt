package win.ambatu.work.feature.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.ui.theme.AmbatuWorkTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddDailyCheckinActivity : ComponentActivity() {

    @Inject
    lateinit var projectRepository: ProjectRepository

    @Inject
    lateinit var sessionManager: SessionManager

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
                AddDailyCheckinScreen(
                    projectId = projectId,
                    sprintId = sprintId,
                    projectRepository = projectRepository,
                    sessionManager = sessionManager,
                    onBackClick = { finish() },
                    onSuccess = {
                        setResult(RESULT_OK)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_PROJECT_ID = "extra_project_id"
        private const val EXTRA_SPRINT_ID = "extra_sprint_id"

        fun createIntent(context: Context, projectId: Long, sprintId: Long): Intent {
            return Intent(context, AddDailyCheckinActivity::class.java).apply {
                putExtra(EXTRA_PROJECT_ID, projectId)
                putExtra(EXTRA_SPRINT_ID, sprintId)
            }
        }
    }
}
