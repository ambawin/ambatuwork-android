package win.ambatu.work

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import win.ambatu.work.intent.MainIntents
import win.ambatu.work.model.DeveloperModel.developers
import win.ambatu.work.ui.theme.AmbatuWorkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nameArray = developers.map { it.name }.toTypedArray()
        val studentIdArray = developers.map { it.studentId }.toTypedArray()
        val studyInfoArray = developers.map { it.studyInfo }.toTypedArray()
        val githubUsernameArray = developers.map { it.githubUsername }.toTypedArray()
        val profileImageArray = developers.map { it.profileImage }.toIntArray()

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onAboutButtonClick = {
                            startActivity(MainIntents.toAbout(
                                this,
                                nameArray,
                                studentIdArray,
                                studyInfoArray,
                                githubUsernameArray,
                                profileImageArray
                            ))
                        },
                        onOurWebsiteButtonClick = {
                            startActivity(Intent(Intent.ACTION_VIEW,
                                "https://work.ambatu.win".toUri()))
                        },
                        onGithubButtonClick = {
                            startActivity(Intent(Intent.ACTION_VIEW,
                                "https://github.com/ambawin/ambatuwork-android".toUri()))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onAboutButtonClick: () -> Unit,
    onOurWebsiteButtonClick: () -> Unit,
    onGithubButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AmbatuWork",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Make everyone do their work!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.placeholder_banner),
                contentDescription = "App banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome to the insider preview of AmbatuWork.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onAboutButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("About the Project")
            }

            OutlinedButton(
                onClick = onOurWebsiteButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Our Website")
            }

            OutlinedButton(
                onClick = onGithubButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Github Repository")
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    AmbatuWorkTheme {
    }
}