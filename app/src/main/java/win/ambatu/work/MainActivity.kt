package win.ambatu.work

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import win.ambatu.work.data.intent.MainIntents
import win.ambatu.work.data.model.placeholderUser
import win.ambatu.work.feature.home.HomeScreen
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = placeholderUser

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme() {
                HomeScreen(
                    user = currentUser,
                    onProfileIconClick = {
                        startActivity(
                            MainIntents.toProfile(
                                this,
                                currentUser
                            )
                        )
                    },
                    onTodaysFocusViewAllClick = {
                        startActivity(
                            MainIntents.toTodaysFocus(
                                this
                            )
                        )
                    },
                    onActiveTeamsViewAllClick = {}
                )
            }
        }
    }
}