package win.ambatu.work.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import win.ambatu.work.R
import win.ambatu.work.controller.TaskController
import win.ambatu.work.controller.TeamController
import win.ambatu.work.data.model.PendingAction
import win.ambatu.work.data.model.Team
import win.ambatu.work.data.model.User
import win.ambatu.work.data.model.getPendingActionsByUserId
import win.ambatu.work.data.model.pendingActionList
import win.ambatu.work.data.model.placeholderUser
import win.ambatu.work.feature.team.TaskFocusCard
import win.ambatu.work.ui.theme.AmbatuWorkTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    user: User,
    onProfileIconClick: () -> Unit,
    onTodaysFocusViewAllClick: () -> Unit,
    onActiveTeamsViewAllClick: () -> Unit
) {
    val hour = LocalTime.now().hour

    val greeting = when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Content(
        user = user,
        greetingText = greeting,
        onProfileIconClick = onProfileIconClick,
        onTodaysFocusViewAllClick = onTodaysFocusViewAllClick,
        onActiveTeamsViewAllClick = onActiveTeamsViewAllClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    user: User = placeholderUser,
    greetingText: String = "Good Morning",
    onProfileIconClick: () -> Unit = {},
    onTodaysFocusViewAllClick: () -> Unit = {},
    onActiveTeamsViewAllClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "AmbatuWork",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onProfileIconClick
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_placeholder),
                            contentDescription = "User profile picture",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {

            item {
                Spacer(
                    modifier = Modifier.size(24.dp)
                )
            }

            item {
                ContentPadding {
                    Column {
                        Text(
                            text = "${greetingText},",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(16.dp)
                )
            }

            // Ranking Cards
            item {
                ContentPadding {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PointsCard(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            text = "Points Earned",
                            number = user.points,
                        )

                        PointsCard(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            text = "Rank in AmbatuWork",
                            number = user.rank,
                            prefix = "#"
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(24.dp)
                )
            }

            // Today's Focus
            item {
                // Title
                ContentPadding {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Today's Focus",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.clickable {
                                onTodaysFocusViewAllClick()
                            }
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }

            // Lists
            val topTasks = TaskController.getAll().take(3)
            items(topTasks.size) { index ->
                ContentPadding {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TaskFocusCard(
                            task = topTasks[index]
                        )
                        Spacer(
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(24.dp)
                )
            }

            // Pending Actions
            item {
                // Title
                ContentPadding {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Pending Actions",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(getPendingActionsByUserId(user.id).size) {
                        PendingActionCard(
                            modifier = Modifier.fillParentMaxWidth(0.82f),
                            pendingAction = pendingActionList[it]
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(24.dp)
                )
            }

            // Active Teams
            item {
                // Title
                ContentPadding {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Active Teams",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.clickable {
                                onActiveTeamsViewAllClick()
                            }
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(3) {
                        TeamCard(
                            modifier = Modifier.fillParentMaxWidth(0.82f),
                            team = TeamController.getAll()[it]
                        )
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(128.dp)
                )
            }
        }
    }
}

@Composable
fun ContentPadding(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 24.dp),
        color = Color.Transparent
    ) {
        content()
    }
}

@Composable
private fun PointsCard(
    modifier: Modifier = Modifier,
    text: String = "Points",
    number: Int = 0,
    prefix: String = ""
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = prefix + number.toString(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PendingActionCard(
    modifier: Modifier,
    pendingAction: PendingAction
) {

    val today = LocalDate.now()
    val dueDate = pendingAction.due.toLocalDate()

    val due = when {
        dueDate.isBefore(today) -> "Missing"
        dueDate == today -> "Today"
        dueDate == today.plusDays(1) -> "Tomorrow"
        else -> pendingAction.due.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH))
    }

    Card(
        modifier = modifier
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = pendingAction.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = due,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )

            Text(
                text = pendingAction.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Button (
                onClick = pendingAction.onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(pendingAction.submitText)
            }
        }
    }
}

@Composable
private fun TeamCard(
    modifier: Modifier,
    team: Team,
    onActionButtonClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(150.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onActionButtonClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = team.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = team.topic,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
@Preview(group = "Home")
private fun HomePreview() {
    AmbatuWorkTheme {
        Content()
    }
}