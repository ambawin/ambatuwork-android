package win.ambatu.work.feature.swagger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class SwaggerUiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AmbatuWorkTheme {
                SwaggerUiScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SwaggerUiActivity::class.java)
        }
    }
}
