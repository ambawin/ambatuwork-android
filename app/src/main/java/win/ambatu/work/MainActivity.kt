package win.ambatu.work

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import win.ambatu.work.core.ComposeApp
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.auth.LoginActivity
import win.ambatu.work.feature.onboarding.OnboardingActivity
import win.ambatu.work.feature.project.ProjectDetailActivity
import win.ambatu.work.feature.project.SprintBoardActivity
import win.ambatu.work.feature.project.PeerReviewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var authRepository: AuthRepository

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sessionManager.hasCompletedOnboarding()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        val token = sessionManager.getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Request POST_NOTIFICATIONS on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Upload FCM token on app launch if authenticated
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            lifecycleScope.launch {
                runCatching {
                    authRepository.updateDeviceToken(token, fcmToken)
                }.onSuccess {
                    Log.d("FCM", "Token uploaded successfully on app launch")
                }.onFailure {
                    Log.w("FCM", "Token upload failed on app launch", it)
                }
            }
        }

        handleNotificationRouting()

        enableEdgeToEdge()
        setContent {
            ComposeApp(
                onLogout = {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val token = sessionManager.getToken()
        if (token != null && sessionManager.hasCompletedOnboarding()) {
            handleNotificationRouting()
        }
    }

    private fun handleNotificationRouting() {
        val type = intent.getStringExtra("type") ?: return
        val projectId = intent.getStringExtra("project_id")?.toLongOrNull() ?: -1L
        if (projectId == -1L) return

        when (type) {
            "project_invitation_accepted" -> {
                val nextIntent = Intent(this, ProjectDetailActivity::class.java).apply {
                    putExtra("extra_project_id", projectId)
                    putExtra("extra_initial_tab", "SETTINGS")
                }
                startActivity(nextIntent)
            }
            "backlog_item_assigned" -> {
                val backlogItemId = intent.getStringExtra("backlog_item_id")?.toLongOrNull() ?: -1L
                val nextIntent = Intent(this, ProjectDetailActivity::class.java).apply {
                    putExtra("extra_project_id", projectId)
                    putExtra("extra_initial_tab", "BACKLOG")
                    putExtra("extra_initial_backlog_item_id", backlogItemId)
                }
                startActivity(nextIntent)
            }
            "sprint_started", "sprint_closed" -> {
                val sprintId = intent.getStringExtra("sprint_id")?.toLongOrNull() ?: -1L
                if (sprintId != -1L) {
                    val nextIntent = SprintBoardActivity.createIntent(this, projectId, sprintId)
                    startActivity(nextIntent)
                }
            }
            "impediment_reported", "impediment_resolved" -> {
                val nextIntent = Intent(this, ProjectDetailActivity::class.java).apply {
                    putExtra("extra_project_id", projectId)
                    putExtra("extra_initial_tab", "DASHBOARD")
                }
                startActivity(nextIntent)
            }
            "peer_review_cycle_opened", "peer_review_cycle_closed" -> {
                val cycleId = intent.getStringExtra("cycle_id")?.toLongOrNull() ?: -1L
                if (cycleId != -1L) {
                    val nextIntent = Intent(this, PeerReviewActivity::class.java).apply {
                        putExtra("extra_project_id", projectId)
                        putExtra("extra_cycle_id", cycleId)
                        putExtra("extra_sprint_name", "Sprint")
                    }
                    startActivity(nextIntent)
                }
            }
        }
    }
}