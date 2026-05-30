package win.ambatu.work.feature.invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import win.ambatu.work.ui.theme.AmbatuWorkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvitationActivity : ComponentActivity() {

    private val viewModel: InvitationViewModel by viewModels()

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
                    },
                    onBackClick = { finish() }
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
