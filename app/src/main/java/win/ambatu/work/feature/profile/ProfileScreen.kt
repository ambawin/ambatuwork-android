package win.ambatu.work.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.Card
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu
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
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.controller.UserController
import win.ambatu.work.data.model.User
import win.ambatu.work.ui.theme.AmbatuWorkTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.Color
import win.ambatu.work.feature.network.UserStatsDto
import win.ambatu.work.ui.theme.LightYellowAmbatu
import win.ambatu.work.ui.theme.LightGreenAmbatu
import win.ambatu.work.ui.theme.GreenAmbatu
import win.ambatu.work.ui.theme.LightRedAmbatu
import win.ambatu.work.ui.theme.RedAmbatu
import win.ambatu.work.ui.theme.LightBlueAmbatu
import win.ambatu.work.ui.theme.BlueAmbatu
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun ProfileScreen(
    user: User,
    isLoading: Boolean = false,
    isStatsLoading: Boolean = false,
    stats: UserStatsDto? = null,
    statsError: String? = null,
    onLogoutClick: () -> Unit,
    onRetryStatsClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onScrumGuideClick: () -> Unit = {}
) {
    Content(
        user = user,
        isLoading = isLoading,
        isStatsLoading = isStatsLoading,
        stats = stats,
        statsError = statsError,
        onLogoutClick = onLogoutClick,
        onRetryStatsClick = onRetryStatsClick,
        onBackClick = onBackClick,
        onScrumGuideClick = onScrumGuideClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    user: User = UserController.getPlaceholderUser(),
    isLoading: Boolean = false,
    isStatsLoading: Boolean = false,
    stats: UserStatsDto? = null,
    statsError: String? = null,
    onLogoutClick: () -> Unit = {},
    onRetryStatsClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onScrumGuideClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YellowAmbatu,
                    titleContentColor = ChocoAmbatu,
                    navigationIconContentColor = ChocoAmbatu,
                    actionIconContentColor = ChocoAmbatu
                ),
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = "My Account",
                        fontWeight = FontWeight.Bold
                    )
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            item {
                Spacer(
                    modifier = Modifier.size(24.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    AsyncImage(
                        model = user.picture,
                        placeholder = painterResource(id = R.drawable.profile_placeholder),
                        error = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "User profile picture",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(128.dp),
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

            }

            item {
                Spacer(
                    modifier = Modifier.size(32.dp)
                )
            }

            item {
                if (isStatsLoading && stats == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = ChocoAmbatu,
                                strokeWidth = 2.5.dp
                            )
                            Text(
                                text = "Loading statistics...",
                                color = ChocoAmbatu,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else if (statsError != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Failed to load stats: $statsError",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            androidx.compose.material3.TextButton(
                                onClick = onRetryStatsClick
                            ) {
                                Text(
                                    text = "Retry",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else if (stats != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "My Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BentoCard(
                                modifier = Modifier.weight(1f),
                                containerColor = LightBlueAmbatu,
                                contentColor = BlueAmbatu,
                                title = "Projects",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = BlueAmbatu
                                    )
                                }
                            ) {
                                Column {
                                    Text(
                                        text = "${stats.projects.totalActive}",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Black,
                                        color = BlueAmbatu
                                    )
                                    Text(
                                        text = "Active Workspaces",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BlueAmbatu.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            BentoCard(
                                modifier = Modifier.weight(1f),
                                containerColor = LightGreenAmbatu,
                                contentColor = GreenAmbatu,
                                title = "Velocity",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GreenAmbatu
                                    )
                                }
                            ) {
                                Column {
                                    Text(
                                        text = "${stats.backlogItems.completedPoints}",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Black,
                                        color = GreenAmbatu
                                    )
                                    Text(
                                        text = "Completed Points",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GreenAmbatu.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            title = "My Tasks",
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = "${stats.backlogItems.assignedTotal}",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Total Assigned Tasks",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }

                                val breakdown = stats.backlogItems.assignedByStatus
                                val total = stats.backlogItems.assignedTotal
                                if (total > 0) {
                                    val inProgressCount = breakdown.inProgress
                                    val doneCount = breakdown.done
                                    val inReviewCount = breakdown.inReview
                                    val todoCount = breakdown.backlog + breakdown.ready + breakdown.selected

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                    ) {
                                        if (doneCount > 0) {
                                            Spacer(
                                                modifier = Modifier
                                                    .weight(doneCount.toFloat())
                                                    .fillMaxHeight()
                                                    .background(GreenAmbatu)
                                            )
                                        }
                                        if (inReviewCount > 0) {
                                            Spacer(
                                                modifier = Modifier
                                                    .weight(inReviewCount.toFloat())
                                                    .fillMaxHeight()
                                                    .background(BlueAmbatu)
                                            )
                                        }
                                        if (inProgressCount > 0) {
                                            Spacer(
                                                modifier = Modifier
                                                    .weight(inProgressCount.toFloat())
                                                    .fillMaxHeight()
                                                    .background(YellowAmbatu)
                                            )
                                        }
                                        if (todoCount > 0) {
                                            Spacer(
                                                modifier = Modifier
                                                    .weight(todoCount.toFloat())
                                                    .fillMaxHeight()
                                                    .background(ChocoAmbatu.copy(alpha = 0.3f))
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        StatusLegendItem(label = "Done", count = doneCount, color = GreenAmbatu)
                                        StatusLegendItem(label = "In Review", count = inReviewCount, color = BlueAmbatu)
                                        StatusLegendItem(label = "In Progress", count = inProgressCount, color = YellowAmbatu)
                                        StatusLegendItem(label = "To Do", count = todoCount, color = ChocoAmbatu.copy(alpha = 0.5f))
                                    }
                                } else {
                                    Text(
                                        text = "No tasks assigned currently",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val openImpediments = stats.impediments.reportedByStatus.open + stats.impediments.reportedByStatus.inProgress
                            BentoCard(
                                modifier = Modifier.weight(1f),
                                containerColor = LightRedAmbatu,
                                contentColor = RedAmbatu,
                                title = "Blockers",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = RedAmbatu
                                    )
                                }
                            ) {
                                Column {
                                    Text(
                                        text = "$openImpediments",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Black,
                                        color = RedAmbatu
                                    )
                                    Text(
                                        text = "Active Impediments",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RedAmbatu.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${stats.impediments.reportedResolved} resolved",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GreenAmbatu,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            BentoCard(
                                modifier = Modifier.weight(1f),
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                title = "Check-ins",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            ) {
                                Column {
                                    Text(
                                        text = "${stats.dailyCheckins.totalSubmitted}",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Submissions",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                    val avgConf = stats.dailyCheckins.averageConfidence ?: 0f
                                    Text(
                                        text = "Confidence: ${String.format("%.1f", avgConf)}/5.0",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ChocoAmbatu,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        BentoCard(
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = LightYellowAmbatu,
                            contentColor = ChocoAmbatu,
                            title = "Peer Reviews",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ChocoAmbatu
                                )
                            }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Average Ratings",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ChocoAmbatu
                                    )
                                    Text(
                                        text = "Received ${stats.peerReviews.receivedTotal} reviews",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ChocoAmbatu.copy(alpha = 0.7f)
                                    )
                                }

                                val scores = stats.peerReviews.receivedAverageScores
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RatingBarRow(label = "Collaboration", score = scores.collaboration)
                                    RatingBarRow(label = "Delivery", score = scores.delivery)
                                    RatingBarRow(label = "Communication", score = scores.communication)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(32.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        onClick = onScrumGuideClick,
                        enabled = !isLoading
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 12.dp)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Scrum Guide icon",
                                tint = ChocoAmbatu
                            )
                            Text(
                                text = "SCRUM Guide",
                                color = ChocoAmbatu,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(16.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(20.dp),
                        onClick = onLogoutClick,
                        enabled = !isLoading
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 12.dp)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout icon",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = if (isLoading) "Logging out..." else "Logout",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(group = "Profile")
private fun ProfilePreview() {
    AmbatuWorkTheme {
        Content()
    }
}

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    title: String,
    icon: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = 0.8f)
                )
                icon?.invoke()
            }
            content()
        }
    }
}

@Composable
fun StatusLegendItem(
    label: String,
    count: Int,
    color: Color
) {
    if (count > 0) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = "$label ($count)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RatingBarRow(
    label: String,
    score: Float?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = ChocoAmbatu.copy(alpha = 0.8f),
            modifier = Modifier.width(100.dp)
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val scoreVal = score ?: 0f
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(ChocoAmbatu.copy(alpha = 0.15f))
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (scoreVal / 5.0f).coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(ChocoAmbatu)
                )
            }
            Text(
                text = if (score != null) String.format("%.2f", scoreVal) else "-",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = ChocoAmbatu,
                modifier = Modifier.width(32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}