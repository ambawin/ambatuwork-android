package win.ambatu.work.feature.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.controller.UserController
import win.ambatu.work.data.model.User
import win.ambatu.work.feature.network.ProjectDto
import win.ambatu.work.ui.theme.AmbatuWorkTheme

@Composable
fun HomeScreen(
    user: User,
    viewModel: HomeViewModel,
    onProfileIconClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Content(
        user = user,
        uiState = uiState,
        onProfileIconClick = onProfileIconClick,
        onCreateProject = viewModel::createProject,
        onAcceptInvitation = viewModel::acceptInvitation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    user: User = UserController.getPlaceholderUser(),
    uiState: HomeUiState = HomeUiState(),
    onProfileIconClick: () -> Unit = {},
    onCreateProject: (String, String, String, Int) -> Unit = { _, _, _, _ -> },
    onAcceptInvitation: (String) -> Unit = {}
) {
    var showCreateSheet by remember { mutableStateOf(false) }
    var showAcceptDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

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
                        AsyncImage(
                            model = user.picture,
                            placeholder = painterResource(id = R.drawable.profile_placeholder),
                            error = painterResource(id = R.drawable.profile_placeholder),
                            contentDescription = "User profile picture",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAcceptDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = "Accept Invitation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Project")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading && uiState.projects.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.projects.isEmpty() && !uiState.isLoading) {
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
                        text = "Create one or join via invitation!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.projects) { project ->
                        ProjectItem(project = project)
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
    }

    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            sheetState = sheetState
        ) {
            CreateProjectForm(
                onDismiss = { showCreateSheet = false },
                onCreate = { name, description, goal, sprint ->
                    onCreateProject(name, description, goal, sprint)
                    showCreateSheet = false
                }
            )
        }
    }

    if (showAcceptDialog) {
        AcceptInvitationDialog(
            onDismiss = { showAcceptDialog = false },
            onConfirm = { token ->
                onAcceptInvitation(token)
                showAcceptDialog = false
            }
        )
    }
}

@Composable
fun ProjectItem(project: ProjectDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            project.description?.let {
                if (it.isNotBlank()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " ${project.memberCount ?: 0} members",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = project.status ?: "Active",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun CreateProjectForm(
    onDismiss: () -> Unit,
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
fun AcceptInvitationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var token by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Accept Invitation") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter the invitation token you received.")
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Token") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(token) },
                enabled = token.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun HomePreview() {
    AmbatuWorkTheme {
        Content()
    }
}
