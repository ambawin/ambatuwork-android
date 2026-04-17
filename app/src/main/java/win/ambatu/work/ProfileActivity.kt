package win.ambatu.work

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import win.ambatu.work.controller.DeveloperController
import win.ambatu.work.data.intent.MainIntents
import win.ambatu.work.data.model.User
import win.ambatu.work.feature.profile.ProfileScreen
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user: User = User(
            id = intent.getIntExtra(MainIntents.EXTRA_USER_ID, 1),
            name = intent.getStringExtra(MainIntents.EXTRA_USER_NAME) ?: "",
            email = intent.getStringExtra(MainIntents.EXTRA_USER_EMAIL) ?: "",
            picture = intent.getIntExtra(MainIntents.EXTRA_USER_PICTURE, R.drawable.profile_placeholder)
        )

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme {
                ProfileScreen(
                    user = user,
                    onNavigationBackClick = {
                        finish()
                    },
                    onAboutClick = {
                        startActivity(MainIntents.toAbout(this, DeveloperController.getAll()))
                    }
                )
            }
        }
    }
}