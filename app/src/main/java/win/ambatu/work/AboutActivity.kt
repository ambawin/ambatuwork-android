package win.ambatu.work

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import win.ambatu.work.intent.MainIntents
import win.ambatu.work.model.DeveloperModel.Developer
import win.ambatu.work.ui.theme.AmbatuWorkTheme


class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nameArray = intent.getStringArrayExtra(MainIntents.EXTRA_NAME).orEmpty()
        val studentIdArray = intent.getStringArrayExtra(MainIntents.EXTRA_STUDENT_ID).orEmpty()
        val studyInfoArray = intent.getStringArrayExtra(MainIntents.EXTRA_STUDY_INFO).orEmpty()
        val githubUsernameArray = intent.getStringArrayExtra(MainIntents.EXTRA_GITHUB_USERNAME).orEmpty()
        val profileImageArray = intent.getIntArrayExtra(MainIntents.EXTRA_PROFILE_IMAGE)

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AboutScreen(
                        nameArray,
                        studentIdArray,
                        studyInfoArray,
                        githubUsernameArray,
                        profileImageArray!!,
                        onShareAppButtonClick = {
                            startActivity(Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_SUBJECT, "Download Insider Preview of our App")
                                    putExtra(Intent.EXTRA_TEXT, "https://github.com/ambawin/ambatuwork-android")
                                    type = "text/plain"
                                },
                                "Share this app"
                            ))
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun AboutScreen(
    nameArray: Array<out String>,
    studentIdArray: Array<out String>,
    studyInfoArray: Array<out String>,
    githubUsernameArray: Array<out String>,
    profileImageArray: IntArray,
    onShareAppButtonClick: () -> Unit
) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "About AmbatuWork",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = "AmbatuWork adalah app kolaborasi yang menjamin meningkatkan rasa semangat, motivasi, dan dorongan antar anggota tim untuk menyelesaikan pekerjaan.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The Team",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(count = minOf(nameArray.size, studentIdArray.size, studyInfoArray.size, githubUsernameArray.size, profileImageArray.size)) { index ->
            DeveloperCard(
                Developer(nameArray[index], studentIdArray[index], studyInfoArray[index], githubUsernameArray[index], profileImageArray[index])
            )
        }

        item {
            Text(
                text = "Support Us",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedButton(
                onClick = onShareAppButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share This App")
            }
        }
    }
}

@Composable
fun DeveloperCard(developer: Developer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = developer.profileImage),
                contentDescription = "${developer.githubUsername} profile pic",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(64.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = developer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = developer.studyInfo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = developer.studentId,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "github.com/${developer.githubUsername}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
