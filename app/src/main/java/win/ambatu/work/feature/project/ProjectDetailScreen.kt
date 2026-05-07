package win.ambatu.work.feature.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.feature.network.BacklogItemDto
import win.ambatu.work.feature.network.DefinitionOfDoneDto
import win.ambatu.work.feature.network.SprintDto
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ProjectTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    BACKLOG("Backlog", Icons.AutoMirrored.Filled.List),
    SPRINT("Sprint", Icons.AutoMirrored.Filled.DirectionsRun),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel,
    onBackClick: () -> Unit,
    onAddBacklogClick: (projectId: Long) -> Unit,
    onSprintClick: (projectId: Long, sprintId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(ProjectTab.DASHBOARD) }
    var selectedBacklogItem by remember { mutableStateOf<BacklogItemDto?>(null) }

    if (selectedBacklogItem != null) {
        BacklogDetailDialog(
            item = selectedBacklogItem!!,
            onDismiss = { selectedBacklogItem = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (selectedTab) {
                            ProjectTab.DASHBOARD -> "Project Dashboard"
                            ProjectTab.BACKLOG -> "Product Backlog"
                            ProjectTab.SPRINT -> "Sprint"
                            ProjectTab.SETTINGS -> "Project Settings"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                ProjectTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.title) },
                        icon = { Icon(tab.icon, contentDescription = tab.title) }
                    )
                }
            }
        },
        floatingActionButton = {
            val role = uiState.project?.myRole?.lowercase()?.trim()
            val canAddBacklog = role != null && (
                role.contains("admin") || 
                role.contains("owner") || 
                role.contains("master") || 
                role.contains("leader")
            )

            if (selectedTab == ProjectTab.BACKLOG && canAddBacklog) {
                FloatingActionButton(
                    onClick = { uiState.project?.id?.let { onAddBacklogClick(it) } },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Backlog Item")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                uiState.project?.let { project ->
                    when (selectedTab) {
                        ProjectTab.DASHBOARD -> DashboardTab(
                            project = project,
                            members = uiState.members
                        )

                        ProjectTab.BACKLOG -> BacklogTab(
                            backlogItems = uiState.backlogItems,
                            onItemClick = { selectedBacklogItem = it }
                        )

                        ProjectTab.SPRINT -> SprintTab(
                            sprints = uiState.sprints,
                            onSprintClick = { sprintId ->
                                uiState.project?.id?.let { projectId ->
                                    onSprintClick(projectId, sprintId)
                                }
                            }
                        )

                        ProjectTab.SETTINGS -> SettingsTab(
                            members = uiState.members
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardTab(
    project: win.ambatu.work.feature.network.ProjectDto,
    members: List<win.ambatu.work.feature.network.ProjectMemberDto>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            val owner = members.find { it.user.id == project.ownerUserId }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = project.description ?: "No description provided",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Product Owner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Product Owner",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = owner?.user?.name ?: "No Owner Assigned",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        AsyncImage(
                            model = owner?.user?.avatarUrl,
                            contentDescription = "Owner avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.profile_placeholder),
                            error = painterResource(R.drawable.profile_placeholder)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccessTime,
                        label = "Sprint Length",
                        value = "${project.defaultSprintLengthDays ?: 0} days"
                    )
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Groups,
                        label = "Team Size",
                        value = "${project.memberCount ?: 0} Members"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                DefinitionOfDoneCard(definitionOfDone = project.definitionOfDone)
            }
        }
    }
}

@Composable
fun BacklogTab(
    backlogItems: List<BacklogItemDto>,
    onItemClick: (BacklogItemDto) -> Unit
) {
    if (backlogItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No backlog items yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                count = backlogItems.size,
                key = { index -> backlogItems[index].id }
            ) { index ->
                BacklogItemCard(
                    item = backlogItems[index],
                    onClick = { onItemClick(backlogItems[index]) }
                )
            }
        }
    }
}

@Composable
fun SprintTab(
    sprints: List<SprintDto>,
    onSprintClick: (Long) -> Unit
) {
    if (sprints.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No sprints created yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                count = sprints.size,
                key = { index -> sprints[index].id }
            ) { index ->
                SprintCard(
                    sprint = sprints[index],
                    onClick = { onSprintClick(sprints[index].id) }
                )
            }
        }
    }
}

@Composable
fun SprintCard(
    sprint: SprintDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sprint.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    sprint.sprintGoal?.let { goal ->
                        Text(
                            text = goal,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                Badge(
                    containerColor = when (sprint.status.lowercase()) {
                        "active" -> MaterialTheme.colorScheme.primary
                        "planned" -> MaterialTheme.colorScheme.secondaryContainer
                        "closed" -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                    contentColor = when (sprint.status.lowercase()) {
                        "active" -> MaterialTheme.colorScheme.onPrimary
                        "planned" -> MaterialTheme.colorScheme.onSecondaryContainer
                        "closed" -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                ) {
                    Text(
                        text = sprint.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val dateRange = remember(sprint.startDate, sprint.endDate) {
                        val start = formatDate(sprint.startDate)
                        val end = formatDate(sprint.endDate)
                        if (start != null && end != null) "$start - $end" else "Dates not set"
                    }
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = dateRange,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${sprint.itemCount ?: 0} Backlog Items",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String?): String? {
    if (dateString == null) return null
    return try {
        val parsed = ZonedDateTime.parse(dateString)
        parsed.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))
    } catch (e: Exception) {
        null
    }
}

@Composable
fun SettingsTab(
    members: List<win.ambatu.work.feature.network.ProjectMemberDto>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Team Members",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(
            count = members.size,
            key = { index -> members[index].user.id ?: index.toLong() }
        ) { index ->
            val member = members[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = member.user.avatarUrl,
                        contentDescription = "Member avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.profile_placeholder),
                        error = painterResource(R.drawable.profile_placeholder)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = member.user.name ?: "Unknown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        member.user.email?.let { email ->
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = member.role.lowercase().split(" ").joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BacklogItemCard(
    item: BacklogItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Badge(
                    containerColor = when (item.type.lowercase()) {
                        "story" -> MaterialTheme.colorScheme.primaryContainer
                        "task" -> MaterialTheme.colorScheme.secondaryContainer
                        "bug" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.tertiaryContainer
                    },
                    contentColor = when (item.type.lowercase()) {
                        "story" -> MaterialTheme.colorScheme.onPrimaryContainer
                        "task" -> MaterialTheme.colorScheme.onSecondaryContainer
                        "bug" -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                    }
                ) {
                    Text(
                        text = item.type.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = item.assignedToUser?.avatarUrl,
                        contentDescription = "Assigned to avatar",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.profile_placeholder),
                        error = painterResource(R.drawable.profile_placeholder)
                    )
                    Column {
                        Text(
                            text = "Assigned to",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = item.assignedToUser?.name ?: "Unassigned",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Estimate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.estimatePoints ?: 0} pts",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun BacklogDetailDialog(
    item: BacklogItemDto,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Description
                if (!item.description.isNullOrBlank()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Metadata chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DetailChip(label = "Type", value = item.type.uppercase(), modifier = Modifier.weight(1f))
                    DetailChip(label = "Status", value = item.status.uppercase(), modifier = Modifier.weight(1f))
                    DetailChip(label = "Priority", value = "#${item.priorityRank}", modifier = Modifier.weight(1f))
                }
                if (item.estimatePoints != null || item.businessValue != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (item.estimatePoints != null) {
                            DetailChip(label = "Estimate", value = "${item.estimatePoints} pts", modifier = Modifier.weight(1f))
                        }
                        if (item.businessValue != null) {
                            DetailChip(label = "Value", value = "${item.businessValue}", modifier = Modifier.weight(1f))
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Acceptance Criteria
                if (!item.acceptanceCriteria.isNullOrEmpty()) {
                    Text(
                        text = "Acceptance Criteria",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    item.acceptanceCriteria.forEach { criteria ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = criteria,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Assignment info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = item.assignedToUser?.avatarUrl,
                        contentDescription = "Assigned user avatar",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.profile_placeholder),
                        error = painterResource(R.drawable.profile_placeholder)
                    )
                    Column {
                        Text(
                            text = "Assigned To",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = item.assignedToUser?.name ?: "Unassigned",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun DetailChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DefinitionOfDoneCard(
    definitionOfDone: DefinitionOfDoneDto?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Definition of Done",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (definitionOfDone != null && definitionOfDone.checklist.isNotEmpty()) {
                definitionOfDone.checklist.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Text(
                    text = "no definition of done yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
