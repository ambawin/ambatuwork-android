package win.ambatu.work.feature.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import win.ambatu.work.ui.components.CircularFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.controller.UserController
import win.ambatu.work.data.model.User
import win.ambatu.work.feature.invitation.InvitationActivity
import win.ambatu.work.feature.network.BacklogItemDto
import win.ambatu.work.feature.network.ProjectDto
import win.ambatu.work.feature.project.AddBacklogItemActivity
import win.ambatu.work.feature.project.AddSprintActivity
import win.ambatu.work.feature.project.BacklogDetailDialog
import win.ambatu.work.feature.project.BacklogTab
import win.ambatu.work.feature.project.DashboardTab
import win.ambatu.work.feature.project.ProjectTab
import win.ambatu.work.feature.project.SettingsTab
import win.ambatu.work.feature.project.SprintBoardActivity
import win.ambatu.work.feature.project.SprintTab
import win.ambatu.work.ui.components.FloatingBottomNavigationBar
import win.ambatu.work.ui.theme.AmbatuWorkTheme
import win.ambatu.work.ui.theme.YellowAmbatu
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu

@Composable
fun HomeScreen(
    user: User,
    viewModel: HomeViewModel,
    onProjectClick: (Long) -> Unit,
    onProfileClick: () -> Unit = {},
    onAddBacklogClick: (Long) -> Unit = {},
    onAddSprintClick: (Long) -> Unit = {},
    onSprintClick: (Long, Long) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    val invitationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.loadProjects()
        }
    }

    val addBacklogLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.loadProjects()
        }
    }

    val addSprintLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.loadProjects()
        }
    }

    Content(
        user = uiState.user ?: user,
        uiState = uiState,
        onCreateProject = viewModel::createProject,
        onInviteUser = viewModel::inviteUser,
        onProjectClick = onProjectClick,
        onSelectProject = viewModel::selectProject,
        onProfileClick = onProfileClick,
        onAddBacklogClick = { projectId ->
            addBacklogLauncher.launch(AddBacklogItemActivity.createIntent(context, projectId))
        },
        onAddSprintClick = { projectId ->
            addSprintLauncher.launch(AddSprintActivity.createIntent(context, projectId))
        },
        onSprintClick = { projectId, sprintId ->
            context.startActivity(SprintBoardActivity.createIntent(context, projectId, sprintId))
        },
        onInvitationsClick = {
            invitationLauncher.launch(InvitationActivity.createIntent(context))
        },
        onUpdateProject = viewModel::updateProject,
        onUpdateBacklogItem = viewModel::updateBacklogItem,
        onArchiveBacklogItem = viewModel::archiveBacklogItem,
        onUpdateMemberRole = viewModel::updateProjectMemberRole,
        onRemoveMember = viewModel::removeProjectMember,
        onRetryStatsClick = { projectId ->
            viewModel.loadProjectStats(projectId)
        },
        onRefresh = {
            viewModel.loadProjects()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    user: User = UserController.getPlaceholderUser(),
    uiState: HomeUiState = HomeUiState(),
    onCreateProject: (String, String, String, Int) -> Unit = { _, _, _, _ -> },
    onInviteUser: (Long, String) -> Unit = { _, _ -> },
    onProjectClick: (Long) -> Unit = {},
    onSelectProject: (ProjectDto) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddBacklogClick: (Long) -> Unit = {},
    onAddSprintClick: (Long) -> Unit = {},
    onSprintClick: (Long, Long) -> Unit = { _, _ -> },
    onInvitationsClick: () -> Unit = {},
    onUpdateProject: (name: String?, description: String?, goal: String?, sprintLength: Int?, wipLimit: Int?) -> Unit = { _, _, _, _, _ -> },
    onUpdateBacklogItem: (id: Long, title: String, description: String?, type: String, estimatePoints: Int?, priority: String?, acceptanceCriteria: List<String>?, assignedToUserId: Long?) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onArchiveBacklogItem: (id: Long) -> Unit = {},
    onUpdateMemberRole: (userId: Long, role: String) -> Unit = { _, _ -> },
    onRemoveMember: (userId: Long) -> Unit = {},
    onRetryStatsClick: (Long) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var showCreateSheet by remember { mutableStateOf(false) }
    var projectSwitcherExpanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(ProjectTab.DASHBOARD) }
    var selectedBacklogItem by remember { mutableStateOf<BacklogItemDto?>(null) }
    val sheetState = rememberModalBottomSheetState()

    if (selectedBacklogItem != null && uiState.selectedProject != null) {
        BacklogDetailDialog(
            item = selectedBacklogItem!!,
            project = uiState.selectedProject!!,
            members = uiState.members,
            onDismiss = { selectedBacklogItem = null },
            onUpdate = { id, title, desc, type, est, priority, ac, assigned ->
                onUpdateBacklogItem(id, title, desc, type, est, priority, ac, assigned)
            },
            onArchive = { id ->
                onArchiveBacklogItem(id)
            }
        )
    }

    Scaffold(
        containerColor = YellowAmbatu,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = ChocoAmbatu,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .shadow(elevation = 2.dp, shape = CircleShape)
                            .background(Color.White, shape = CircleShape)
                    ) {
                        AsyncImage(
                            model = user.picture,
                            placeholder = painterResource(id = R.drawable.profile_placeholder),
                            error = painterResource(id = R.drawable.profile_placeholder),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                title = {
                    ExposedDropdownMenuBox(
                        expanded = projectSwitcherExpanded,
                        onExpandedChange = { projectSwitcherExpanded = it }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .menuAnchor()
                                .widthIn(min = 200.dp)
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(50))
                                .background(WhiteAmbatu, shape = RoundedCornerShape(50))
                                .clickable { projectSwitcherExpanded = true }
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = uiState.selectedProject?.name ?: "Select Project",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChocoAmbatu,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        val menuColorScheme = MaterialTheme.colorScheme.copy(
                            surface = WhiteAmbatu,
                            surfaceContainer = WhiteAmbatu,
                            surfaceContainerLow = WhiteAmbatu,
                            surfaceContainerHigh = WhiteAmbatu,
                            surfaceVariant = WhiteAmbatu
                        )
                        val menuShapes = MaterialTheme.shapes.copy(
                            extraSmall = RoundedCornerShape(24.dp),
                            small = RoundedCornerShape(24.dp),
                            medium = RoundedCornerShape(24.dp),
                            large = RoundedCornerShape(24.dp)
                        )

                        MaterialTheme(
                            colorScheme = menuColorScheme,
                            shapes = menuShapes
                        ) {
                            ExposedDropdownMenu(
                                expanded = projectSwitcherExpanded,
                                onDismissRequest = { projectSwitcherExpanded = false },
                                modifier = Modifier.exposedDropdownSize()
                            ) {
                                uiState.projects.forEach { project ->
                                    DropdownMenuItem(
                                        text = { Text(project.name, color = ChocoAmbatu, fontWeight = FontWeight.Medium) },
                                        onClick = {
                                            onSelectProject(project)
                                            projectSwitcherExpanded = false
                                        }
                                    )
                                }
                                if (uiState.projects.isNotEmpty()) {
                                    HorizontalDivider(color = ChocoAmbatu.copy(alpha = 0.15f))
                                }
                                DropdownMenuItem(
                                    text = { Text("Add New Project", color = ChocoAmbatu, fontWeight = FontWeight.SemiBold) },
                                    leadingIcon = { Icon(Icons.Default.Add, null, tint = ChocoAmbatu) },
                                    onClick = {
                                        projectSwitcherExpanded = false
                                        showCreateSheet = true
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onInvitationsClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = WhiteAmbatu,
                            contentColor = ChocoAmbatu
                        ),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .shadow(elevation = 2.dp, shape = CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Invitations"
                        )
                    }
                }
            )
        },
        bottomBar = {
            FloatingBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            if (uiState.selectedProject != null) {
                val role = uiState.selectedProject.myRole?.lowercase()?.trim()
                val canEdit = role != null && (
                        role.contains("admin") ||
                                role.contains("owner") ||
                                role.contains("master") ||
                                role.contains("leader")
                        )

                if (selectedTab == ProjectTab.BACKLOG && canEdit) {
                    CircularFloatingActionButton(
                        onClick = { onAddBacklogClick(uiState.selectedProject.id) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Backlog Item")
                    }
                }

                if (selectedTab == ProjectTab.SPRINT && canEdit) {
                    CircularFloatingActionButton(
                        onClick = { onAddSprintClick(uiState.selectedProject.id) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Sprint")
                    }
                }
            } else {
                CircularFloatingActionButton(
                    onClick = { showCreateSheet = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Project")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (uiState.isLoading && uiState.selectedProject == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.selectedProject == null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No projects found",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create one to get started!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showCreateSheet = true }) {
                        Text("Create Project")
                    }
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = uiState.isLoading || uiState.isStatsLoading,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (selectedTab) {
                        ProjectTab.DASHBOARD -> DashboardTab(
                            project = uiState.selectedProject,
                            members = uiState.members,
                            stats = uiState.stats,
                            isStatsLoading = uiState.isStatsLoading,
                            statsError = uiState.statsError,
                            onRetryStatsClick = {
                                uiState.selectedProject?.id?.let { onRetryStatsClick(it) }
                            }
                        )
                        ProjectTab.BACKLOG -> BacklogTab(
                            backlogItems = uiState.backlogItems,
                            onItemClick = { item -> selectedBacklogItem = item }
                        )
                        ProjectTab.SPRINT -> SprintTab(
                            sprints = uiState.sprints,
                            sprintAssignees = uiState.sprintAssignees,
                            onSprintClick = { sprintId ->
                                uiState.selectedProject?.id?.let { projectId ->
                                    onSprintClick(projectId, sprintId)
                                }
                            }
                        )
                        ProjectTab.SETTINGS -> {
                            uiState.selectedProject?.let { project ->
                                SettingsTab(
                                    project = project,
                                    members = uiState.members,
                                    onUpdateProject = { name, desc, goal, length, wip ->
                                        onUpdateProject(name, desc, goal, length, wip)
                                    },
                                    onUpdateMemberRole = { userId, role ->
                                        onUpdateMemberRole(userId, role)
                                    },
                                    onRemoveMember = { userId ->
                                        onRemoveMember(userId)
                                    },
                                    onInviteMember = { email ->
                                        onInviteUser(project.id, email)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }

        if (showCreateSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCreateSheet = false },
                sheetState = sheetState
            ) {
                CreateProjectForm(
                onCreate = { name, description, goal, sprint ->
                    onCreateProject(name, description, goal, sprint)
                    showCreateSheet = false
                }
            )
            }
        }
    }
}

@Composable
fun CreateProjectForm(
    onCreate: (String, String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var sprintLength by remember { mutableStateOf("14") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(bottom = 32.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Create New Project", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Project Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Project Description") },
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
        Button(
            onClick = {
                onCreate(name, description, goal, sprintLength.toIntOrNull() ?: 14)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && goal.isNotBlank()
        ) {
            Text("Create Project")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HomePreview() {
    AmbatuWorkTheme {
        Content()
    }
}

