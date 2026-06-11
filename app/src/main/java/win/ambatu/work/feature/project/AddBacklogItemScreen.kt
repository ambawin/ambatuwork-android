package win.ambatu.work.feature.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.CreateBacklogItemRequest
import win.ambatu.work.feature.network.ProjectMemberDto
import win.ambatu.work.ui.components.AmbatuDropdownField
import win.ambatu.work.ui.components.AmbatuSimpleTextField
import win.ambatu.work.ui.components.AmbatuTextField
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBacklogItemScreen(
    projectId: Long,
    projectRepository: ProjectRepository,
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("story") }
    var priority by remember { mutableStateOf("medium") }
    var estimatePoints by remember { mutableStateOf(0) }
    var assignedToUserId by remember { mutableStateOf<Long?>(null) }
    var acceptanceCriteria by remember { mutableStateOf(listOf<String>()) }
    
    var members by remember { mutableStateOf<List<ProjectMemberDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val itemTypes = listOf("story", "task", "bug", "improvements")
    val priorityOptions = listOf("highest", "high", "medium", "low", "lowest")
    var expandedType by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedMembers by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        isLoading = true
        try {
            val token = sessionManager.getToken()
            if (token != null) {
                members = projectRepository.getProjectMembers(token, projectId)
            }
        } catch (e: Exception) {
            error = "Failed to load members: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = YellowAmbatu,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Backlog Item", fontWeight = FontWeight.Bold) },
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
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ChocoAmbatu)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AmbatuTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                AmbatuTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Type Dropdown
                AmbatuDropdownField(
                    value = type.uppercase(),
                    label = "Type",
                    expanded = expandedType,
                    onExpandedChange = { expandedType = it }
                ) {
                    itemTypes.forEach { itemType ->
                        DropdownMenuItem(
                            text = { Text(itemType.uppercase()) },
                            onClick = {
                                type = itemType
                                expandedType = false
                            }
                        )
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
                            fontWeight = FontWeight.Medium,
                            color = ChocoAmbatu
                        )
                        Slider(
                            value = estimatePoints.toFloat(),
                            onValueChange = { estimatePoints = it.roundToInt() },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = ChocoAmbatu,
                                activeTrackColor = ChocoAmbatu,
                                inactiveTrackColor = ChocoAmbatu.copy(alpha = 0.24f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Priority Dropdown
                    AmbatuDropdownField(
                        value = priority.uppercase(),
                        label = "Priority",
                        expanded = expandedPriority,
                        onExpandedChange = { expandedPriority = it }
                    ) {
                        priorityOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt.uppercase()) },
                                onClick = {
                                    priority = opt
                                    expandedPriority = false
                                }
                            )
                        }
                    }
                }

                Text("Acceptance Criteria", style = MaterialTheme.typography.titleMedium, color = ChocoAmbatu)
                
                acceptanceCriteria.forEachIndexed { index, criteria ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AmbatuSimpleTextField(
                            value = criteria,
                            onValueChange = { newValue ->
                                val newList = acceptanceCriteria.toMutableList()
                                newList[index] = newValue
                                acceptanceCriteria = newList
                            },
                            placeholder = "e.g. User can toggle dark mode",
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            val newList = acceptanceCriteria.toMutableList()
                            newList.removeAt(index)
                            acceptanceCriteria = newList
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = ChocoAmbatu
                            )
                        }
                    }
                }
                
                TextButton(
                    onClick = { acceptanceCriteria = acceptanceCriteria + "" },
                    colors = ButtonDefaults.textButtonColors(contentColor = ChocoAmbatu),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ChocoAmbatu)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Criteria")
                }

                // Member Dropdown for Assignment
                val selectedMember = members.find { it.user.id == assignedToUserId }
                AmbatuDropdownField(
                    value = selectedMember?.user?.name ?: "Unassigned",
                    label = "Assign To",
                    expanded = expandedMembers,
                    onExpandedChange = { expandedMembers = it }
                ) {
                    DropdownMenuItem(
                        text = { Text("Unassigned") },
                        onClick = {
                            assignedToUserId = null
                            expandedMembers = false
                        }
                    )
                    members.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member.user.name ?: "Unknown") },
                            onClick = {
                                assignedToUserId = member.user.id
                                expandedMembers = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Title is required")
                            }
                            return@Button
                        }
                        
                        isSubmitting = true
                        scope.launch {
                            try {
                                val token = sessionManager.getToken()
                                if (token != null) {
                                    val request = CreateBacklogItemRequest(
                                        title = title,
                                        description = description.ifBlank { null },
                                        type = type,
                                        priority = priority,
                                        estimatePoints = estimatePoints,
                                        acceptanceCriteria = acceptanceCriteria.filter { it.isNotBlank() }.ifEmpty { null },
                                        assignedToUserId = assignedToUserId
                                    )
                                    projectRepository.createBacklogItem(token, projectId, request)
                                    onSuccess()
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error: ${e.message}")
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocoAmbatu,
                        contentColor = WhiteAmbatu,
                        disabledContainerColor = ChocoAmbatu.copy(alpha = 0.5f),
                        disabledContentColor = WhiteAmbatu.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting && title.isNotBlank()
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WhiteAmbatu,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Backlog Item")
                    }
                }
            }
        }
    }
}
