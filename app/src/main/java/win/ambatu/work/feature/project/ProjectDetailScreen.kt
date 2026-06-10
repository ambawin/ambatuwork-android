package win.ambatu.work.feature.project

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import win.ambatu.work.ui.components.CircularFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuAnchorType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Slider
import kotlin.math.roundToInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.feature.network.BacklogItemDto
import win.ambatu.work.feature.network.UserDto
import win.ambatu.work.feature.network.DefinitionOfDoneDto
import win.ambatu.work.feature.network.SprintDto
import win.ambatu.work.ui.components.FloatingBottomNavigationBar
import androidx.compose.ui.text.style.TextOverflow
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.DarkChocoAmbatu
import win.ambatu.work.ui.theme.GreenAmbatu
import win.ambatu.work.ui.theme.BlueAmbatu
import win.ambatu.work.ui.theme.RedAmbatu
import win.ambatu.work.ui.theme.LightChocoAmbatu
import win.ambatu.work.ui.theme.Typography
import win.ambatu.work.ui.theme.WhiteAmbatu
import win.ambatu.work.ui.theme.LimeGreenAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu
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
    onAddSprintClick: (projectId: Long) -> Unit,
    onSprintClick: (projectId: Long, sprintId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(ProjectTab.DASHBOARD) }
    var selectedBacklogItem by remember { mutableStateOf<BacklogItemDto?>(null) }

    if (selectedBacklogItem != null && uiState.project != null) {
        BacklogDetailDialog(
            item = selectedBacklogItem!!,
            project = uiState.project!!,
            members = uiState.members,
            onDismiss = { selectedBacklogItem = null },
            onUpdate = { id, title, desc, type, est, value, ac, assignee ->
                viewModel.updateBacklogItem(id, title, desc, type, est, value, ac, assignee)
            },
            onArchive = { id ->
                viewModel.archiveBacklogItem(id)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            ProjectTab.DASHBOARD -> "Project Dashboard"
                            ProjectTab.BACKLOG -> "Product Backlog"
                            ProjectTab.SPRINT -> "Sprint"
                            ProjectTab.SETTINGS -> "Project Settings"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YellowAmbatu,
                    titleContentColor = ChocoAmbatu,
                    navigationIconContentColor = ChocoAmbatu,
                    actionIconContentColor = ChocoAmbatu
                )
            )
        },
        bottomBar = {
            FloatingBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
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
                CircularFloatingActionButton(
                    onClick = { uiState.project?.id?.let { onAddBacklogClick(it) } }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Backlog Item")
                }
            }

            if (selectedTab == ProjectTab.SPRINT && canAddBacklog) {
                CircularFloatingActionButton(
                    onClick = { uiState.project?.id?.let { onAddSprintClick(it) } }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Sprint")
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
                            sprintAssignees = uiState.sprintAssignees,
                            onSprintClick = { sprintId ->
                                uiState.project?.id?.let { projectId ->
                                    onSprintClick(projectId, sprintId)
                                }
                            }
                        )

                        ProjectTab.SETTINGS -> SettingsTab(
                            project = project,
                            members = uiState.members,
                            onUpdateProject = { name, desc, goal, length, wip ->
                                viewModel.updateProject(name, desc, goal, length, wip)
                            },
                            onUpdateMemberRole = { userId, role ->
                                viewModel.updateProjectMemberRole(userId, role)
                            },
                            onRemoveMember = { userId ->
                                viewModel.removeProjectMember(userId)
                            },
                            onInviteMember = { email ->
                                viewModel.inviteUser(email)
                            }
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
                    text = "Hello, Dap!",
                    style = Typography.headlineLarge,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = ChocoAmbatu
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
    sprintAssignees: Map<Long, List<UserDto>>,
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
                val sprint = sprints[index]
                SprintCard(
                    sprint = sprint,
                    assignees = sprintAssignees[sprint.id] ?: emptyList(),
                    onClick = { onSprintClick(sprint.id) }
                )
            }
        }
    }
}

@Composable
fun SprintCard(
    sprint: SprintDto,
    assignees: List<UserDto>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = WhiteAmbatu
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sprint.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChocoAmbatu
                    )
                    sprint.sprintGoal?.let { goal ->
                        if (goal.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkChocoAmbatu.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                val statusLabel = when (sprint.status.lowercase()) {
                    "closed" -> "Finished"
                    "active" -> "Active"
                    "planned" -> "Planned"
                    else -> sprint.status.uppercase()
                }

                val badgeColor = when (sprint.status.lowercase()) {
                    "closed" -> GreenAmbatu
                    "active" -> BlueAmbatu
                    "planned" -> ChocoAmbatu
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = WhiteAmbatu
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateRange = remember(sprint.startDate, sprint.endDate) {
                    val start = formatDate(sprint.startDate)
                    val end = formatDate(sprint.endDate)
                    if (start != null && end != null) "$start - $end" else "Dates not set"
                }

                Text(
                    text = dateRange,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocoAmbatu.copy(alpha = 0.6f)
                )

                // Avatar Pile
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-12).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    assignees.take(3).forEach { assignee ->
                        AsyncImage(
                            model = assignee.avatarUrl,
                            contentDescription = "Assignee avatar",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(2.dp, WhiteAmbatu, CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.profile_placeholder),
                            error = painterResource(R.drawable.profile_placeholder)
                        )
                    }
                    if (assignees.size > 3) {
                        Surface(
                            color = ChocoAmbatu,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(36.dp)
                                .border(2.dp, WhiteAmbatu, CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "+${assignees.size - 3}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = WhiteAmbatu
                                )
                            }
                        }
                    }
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
    project: win.ambatu.work.feature.network.ProjectDto,
    members: List<win.ambatu.work.feature.network.ProjectMemberDto>,
    onUpdateProject: (name: String?, description: String?, goal: String?, sprintLength: Int?, wipLimit: Int?) -> Unit,
    onUpdateMemberRole: (userId: Long, role: String) -> Unit,
    onRemoveMember: (userId: Long) -> Unit,
    onInviteMember: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var emailToInvite by remember { mutableStateOf("") }

    val role = project.myRole?.lowercase()?.trim()
    val canEdit = role != null && (
        role.contains("admin") ||
        role.contains("owner") ||
        role.contains("master") ||
        role.contains("leader")
    )

    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = {
                showInviteDialog = false
                emailToInvite = ""
            },
            title = { Text("Invite New Member") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Enter the email address of the user you want to invite to this project.")
                    OutlinedTextField(
                        value = emailToInvite,
                        onValueChange = { emailToInvite = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onInviteMember(emailToInvite)
                        showInviteDialog = false
                        emailToInvite = ""
                    },
                    enabled = emailToInvite.isNotBlank()
                ) {
                    Text("Invite")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showInviteDialog = false
                        emailToInvite = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {
        EditProjectDialog(
            project = project,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, desc, goal, length, wip ->
                onUpdateProject(name, desc, goal, length, wip)
                showEditDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Project Configuration",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (canEdit) {
                            TextButton(onClick = { showEditDialog = true }) {
                                Text("Modify")
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "Goal:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = project.productGoal ?: "No goal defined",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sprint Duration",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "${project.defaultSprintLengthDays ?: 14} Days",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WIP Limit",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = project.wipLimitPerMember?.let { "$it items" } ?: "None",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Team Members",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                if (canEdit) {
                    TextButton(onClick = { showInviteDialog = true }) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Invite")
                    }
                }
            }
        }
        items(
            count = members.size,
            key = { index -> members[index].user.id ?: index.toLong() }
        ) { index ->
            val member = members[index]
            var menuExpanded by remember { mutableStateOf(false) }
            val isMemberOwner = member.role.lowercase().contains("owner")
            val showMenuButton = canEdit && !isMemberOwner

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(40.dp),
                colors = CardDefaults.cardColors(
                    containerColor = WhiteAmbatu
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    AsyncImage(
                        model = member.user.avatarUrl,
                        contentDescription = "Member avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.profile_placeholder),
                        error = painterResource(R.drawable.profile_placeholder)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = member.user.name ?: "Unknown",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.Bold,
                            color = ChocoAmbatu
                        )
                        member.user.email?.let { email ->
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val roleBadgeText = member.role.lowercase().split(" ").joinToString(" ") { word -> 
                            word.replaceFirstChar { it.uppercase() } 
                        }
                        
                        val badgeBgColor = when (member.role.lowercase().trim()) {
                            "owner" -> LimeGreenAmbatu
                            "supervisor" -> ChocoAmbatu
                            else -> LightChocoAmbatu
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = badgeBgColor
                        ) {
                            Text(
                                text = roleBadgeText,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = WhiteAmbatu,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                            )
                        }
                    }
                    if (showMenuButton) {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Member options"
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                val currentRole = member.role.lowercase()
                                if (currentRole != "supervisor") {
                                    DropdownMenuItem(
                                        text = { Text("Promote to Supervisor") },
                                        onClick = {
                                            menuExpanded = false
                                            member.user.id?.let { onUpdateMemberRole(it, "supervisor") }
                                        }
                                    )
                                }
                                if (currentRole != "member") {
                                    DropdownMenuItem(
                                        text = { Text("Demote to Member") },
                                        onClick = {
                                            menuExpanded = false
                                            member.user.id?.let { onUpdateMemberRole(it, "member") }
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("Remove Member", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        menuExpanded = false
                                        member.user.id?.let { onRemoveMember(it) }
                                    }
                                )
                            }
                        }
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = WhiteAmbatu
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChocoAmbatu
                    )
                    if (!item.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkChocoAmbatu.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                AsyncImage(
                    model = item.assignedToUser?.avatarUrl,
                    contentDescription = "Assigned to avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.profile_placeholder),
                    error = painterResource(R.drawable.profile_placeholder)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Points Pill
                Surface(
                    color = ChocoAmbatu,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "${item.estimatePoints ?: 0}pts",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = WhiteAmbatu
                        )
                    }
                }

                // Type Pill
                Surface(
                    color = when (item.type.lowercase()) {
                        "story" -> GreenAmbatu
                        "task" -> BlueAmbatu
                        "bug" -> RedAmbatu
                        else -> LightChocoAmbatu
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = item.type.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = WhiteAmbatu
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacklogDetailDialog(
    item: BacklogItemDto,
    project: win.ambatu.work.feature.network.ProjectDto,
    members: List<win.ambatu.work.feature.network.ProjectMemberDto>,
    onDismiss: () -> Unit,
    onUpdate: (id: Long, title: String, description: String?, type: String, estimatePoints: Int?, priority: String?, acceptanceCriteria: List<String>?, assignedToUserId: Long?) -> Unit,
    onArchive: (id: Long) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    val role = project.myRole?.lowercase()?.trim()
    val canEdit = role != null && (
        role.contains("admin") ||
        role.contains("owner") ||
        role.contains("master") ||
        role.contains("leader")
    )

    var title by remember { mutableStateOf(item.title) }
    var description by remember { mutableStateOf(item.description ?: "") }
    var type by remember { mutableStateOf(item.type) }
    var estimatePoints by remember { mutableStateOf(item.estimatePoints ?: 0) }
    var priority by remember { mutableStateOf(item.priority) }
    var assignedToUserId by remember { mutableStateOf(item.assignedToUserId) }
    val acceptanceCriteria by remember { mutableStateOf(item.acceptanceCriteria ?: emptyList()) }

    var memberDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var priorityDropdownExpanded by remember { mutableStateOf(false) }
    val priorityOptions = listOf("highest", "high", "medium", "low", "lowest")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (isEditing) {
                Button(
                    onClick = {
                        onUpdate(
                            item.id,
                            title,
                            description.takeIf { it.isNotBlank() },
                            type,
                            estimatePoints,
                            priority,
                            acceptanceCriteria,
                            assignedToUserId
                        )
                        onDismiss()
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text("Save")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing) {
                    TextButton(
                        onClick = { onArchive(item.id); onDismiss() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Archive")
                    }
                    TextButton(onClick = { isEditing = false }) {
                        Text("Cancel")
                    }
                } else if (canEdit) {
                    TextButton(onClick = { isEditing = true }) {
                        Text("Edit")
                    }
                }
            }
        },
        title = {
            if (isEditing) {
                Text(text = "Edit Backlog Item", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            } else {
                Text(text = item.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    // Type Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = typeDropdownExpanded,
                        onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = type.uppercase(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false }
                        ) {
                            listOf("story", "task", "bug", "improvement").forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.uppercase()) },
                                    onClick = {
                                        type = t
                                        typeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Member Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = memberDropdownExpanded,
                        onExpandedChange = { memberDropdownExpanded = !memberDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val currentMember = members.find { it.user.id == assignedToUserId }
                        OutlinedTextField(
                            value = currentMember?.user?.name ?: "Unassigned",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assigned To") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = memberDropdownExpanded,
                            onDismissRequest = { memberDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Unassigned") },
                                onClick = {
                                    assignedToUserId = null
                                    memberDropdownExpanded = false
                                }
                            )
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.user.name ?: "") },
                                    onClick = {
                                        assignedToUserId = m.user.id
                                        memberDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Estimate Points: $estimatePoints",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = estimatePoints.toFloat(),
                                onValueChange = { estimatePoints = it.roundToInt() },
                                valueRange = 0f..100f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Priority Selector Dropdown
                        ExposedDropdownMenuBox(
                            expanded = priorityDropdownExpanded,
                            onExpandedChange = { priorityDropdownExpanded = !priorityDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = priority.uppercase(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Priority") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = priorityDropdownExpanded,
                                onDismissRequest = { priorityDropdownExpanded = false }
                            ) {
                                priorityOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt.uppercase()) },
                                        onClick = {
                                            priority = opt
                                            priorityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                } else {
                    // Read-only Mode (original UI layout)
                    if (!item.description.isNullOrBlank()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        DetailChip(label = "Type", value = item.type.uppercase(), modifier = Modifier.weight(1f))
                        DetailChip(label = "Status", value = item.status.uppercase(), modifier = Modifier.weight(1f))
                        DetailChip(label = "Priority", value = item.priority.uppercase(), modifier = Modifier.weight(1f))
                    }

                    if (item.estimatePoints != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            DetailChip(label = "Estimate", value = "${item.estimatePoints} pts", modifier = Modifier.weight(1f))
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

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
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProjectDialog(
    project: win.ambatu.work.feature.network.ProjectDto,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?, goal: String, sprintLength: Int, wipLimit: Int?) -> Unit
) {
    var name by remember { mutableStateOf(project.name) }
    var description by remember { mutableStateOf(project.description ?: "") }
    var goal by remember { mutableStateOf(project.productGoal ?: "") }
    var sprintLength by remember { mutableStateOf(project.defaultSprintLengthDays?.toString() ?: "14") }
    var wipLimit by remember { mutableStateOf(project.wipLimitPerMember?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Project Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Product Goal") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sprintLength,
                    onValueChange = { sprintLength = it },
                    label = { Text("Sprint Length (days)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = wipLimit,
                    onValueChange = { wipLimit = it },
                    label = { Text("WIP Limit (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        name,
                        description.takeIf { it.isNotBlank() },
                        goal,
                        sprintLength.toIntOrNull() ?: 14,
                        wipLimit.toIntOrNull()
                    )
                },
                enabled = name.isNotBlank() && goal.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
