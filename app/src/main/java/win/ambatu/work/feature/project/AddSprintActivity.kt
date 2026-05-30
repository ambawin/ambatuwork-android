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
class AddSprintActivity : ComponentActivity() {

    @Inject
    lateinit var projectRepository: ProjectRepository

    @Inject
    lateinit var sessionManager: SessionManager

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
                AddSprintScreen(
                    projectId = projectId,
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

        fun createIntent(context: Context, projectId: Long): Intent {
            return Intent(context, AddSprintActivity::class.java).apply {
                putExtra(EXTRA_PROJECT_ID, projectId)
            }
        }
    }
}
