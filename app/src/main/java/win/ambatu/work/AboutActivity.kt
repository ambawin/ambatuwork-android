package win.ambatu.work

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
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

        val developers = nameArray.indices.map { index ->
            Developer(
                name = nameArray[index],
                studentId = studentIdArray[index],
                studyInfo = studyInfoArray[index],
                githubUsername = githubUsernameArray[index],
                profileImage = profileImageArray!![index]
            )
        }

        enableEdgeToEdge()
        setContent {
            AmbatuWorkTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AboutScreen(
                        developers = developers,
                        onBackButtonClick = {
                            finish()
                        },
                        onShareAppButtonClick = {
                            startActivity(Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_SUBJECT, "Download Insider Preview of our App")
                                    putExtra(Intent.EXTRA_TEXT, "Check out AmbatuWork App!\n\nhttps://github.com/ambawin/ambatuwork-android")
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
    developers: List<Developer>,
    onBackButtonClick: () -> Unit,
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
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackButtonClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "About AmbatuWork",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            Text(
                text = "AmbatuWork is a collaboration app that helps teams stay aligned, motivated, and focused on getting work done.",
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
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(developers) { developer ->
            DeveloperCard(developer)
        }

        item {
            Text(
                text = "Support Us",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
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
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://github.com/${developer.githubUsername}".toUri()
                )
                context.startActivity(intent)
            },
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
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
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