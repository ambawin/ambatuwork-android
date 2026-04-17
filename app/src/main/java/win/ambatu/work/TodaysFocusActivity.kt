package win.ambatu.work

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import win.ambatu.work.feature.team.TodaysFocusScreen
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class TodaysFocusActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme {
                TodaysFocusScreen(
                    onNavigationBackClick = {
                        finish()
                    }
                )
            }
        }
    }
}