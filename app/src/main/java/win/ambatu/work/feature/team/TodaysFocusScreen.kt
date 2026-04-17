package win.ambatu.work.feature.team

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import win.ambatu.work.R
import win.ambatu.work.controller.TaskController
import win.ambatu.work.data.model.Task
import win.ambatu.work.feature.home.ContentPadding
import win.ambatu.work.ui.theme.AmbatuWorkTheme
import win.ambatu.work.util.DateUtils
import java.time.LocalDate

@Composable
fun TodaysFocusScreen(
    onNavigationBackClick: () -> Unit
) {
    Content(
        onNavigationBackClick = onNavigationBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    onNavigationBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .padding(top = 8.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Today's Focus",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigationBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        val tasks = TaskController.getAll()
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            items(tasks.size) { index ->
                val task = tasks[index]
                ContentPadding {
                    Column {
                        TaskFocusCard(
                            task = task
                        )
                        Spacer(
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskFocusCard(
    task: Task,
    onActionButtonClick: () -> Unit = {}
) {
    val team = TaskController.getTeamFromTaskId(task.teamId)
    val due = DateUtils.formatDueDate(task.due)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp),
        onClick = onActionButtonClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = team?.picture ?: R.drawable.placeholder_banner),
                contentDescription = "Focus Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$due - ${team?.name}",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "+ ${task.point}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Pts")
            }
        }
    }
}


@Composable
@Preview(group = "Today's Focus")
private fun TodaysFocusPreview() {
    AmbatuWorkTheme {
        Content()
    }
}