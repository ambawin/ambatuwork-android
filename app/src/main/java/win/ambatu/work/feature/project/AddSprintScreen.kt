package win.ambatu.work.feature.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.BacklogItemDto
import win.ambatu.work.feature.network.CreateSprintRequest
import win.ambatu.work.ui.components.AmbatuTextField
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSprintScreen(
    projectId: Long,
    projectRepository: ProjectRepository,
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sprintGoal by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedBacklogItemIds by remember { mutableStateOf(setOf<Long>()) }

    var backlogItems by remember { mutableStateOf<List<BacklogItemDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        isLoading = true
        try {
            val token = sessionManager.getToken()
            if (token != null) {
                // Fetch backlog items to select for the sprint
                val items = projectRepository.getProjectBacklogItems(token, projectId)
                // Filter items that are not already in a sprint or archived if possible
                // For now, showing all non-done items might be a good start
                backlogItems = items.filter { it.status.lowercase() != "done" && it.status.lowercase() != "archived" }
            }
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Failed to load backlog items: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        startDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        endDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create New Sprint", fontWeight = FontWeight.Bold) },
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
                CircularProgressIndicator()
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
                    value = name,
                    onValueChange = { if (it.length <= 255) name = it },
                    label = "Sprint Name",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("${name.length}/255") }
                )

                AmbatuTextField(
                    value = sprintGoal,
                    onValueChange = { if (it.length <= 5000) sprintGoal = it },
                    label = "Sprint Goal",
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    supportingText = { Text("${sprintGoal.length}/5000") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AmbatuTextField(
                        value = startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = "Start Date",
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Start Date",
                                    tint = ChocoAmbatu
                                )
                            }
                        }
                    )
                    AmbatuTextField(
                        value = endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = "End Date",
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = { showEndDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select End Date",
                                    tint = ChocoAmbatu
                                )
                            }
                        }
                    )
                }

                Text(
                    text = "Select Backlog Items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (backlogItems.isEmpty()) {
                    Text(
                        text = "No available backlog items to add to sprint.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(backlogItems) { item ->
                                val isSelected = selectedBacklogItemIds.contains(item.id)
                                ListItem(
                                    headlineContent = { Text(item.title) },
                                    supportingContent = { Text("${item.type.uppercase()} • ${item.estimatePoints ?: 0} pts") },
                                    trailingContent = {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                selectedBacklogItemIds = if (checked) {
                                                    selectedBacklogItemIds + item.id
                                                } else {
                                                    selectedBacklogItemIds - item.id
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        selectedBacklogItemIds = if (isSelected) {
                                            selectedBacklogItemIds - item.id
                                        } else {
                                            selectedBacklogItemIds + item.id
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val errorMsg = when {
                            name.isBlank() -> "Name is required"
                            sprintGoal.isBlank() -> "Sprint goal is required"
                            startDate == null -> "Start date is required"
                            endDate == null -> "End date is required"
                            endDate!!.isBefore(startDate) -> "End date must be on or after start date"
                            selectedBacklogItemIds.isEmpty() -> "Select at least one backlog item"
                            else -> null
                        }

                        if (errorMsg != null) {
                            scope.launch { snackbarHostState.showSnackbar(errorMsg) }
                            return@Button
                        }
                        
                        isSubmitting = true
                        scope.launch {
                            try {
                                val token = sessionManager.getToken()
                                if (token != null) {
                                    val request = CreateSprintRequest(
                                        name = name,
                                        sprintGoal = sprintGoal,
                                        startDate = startDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                        endDate = endDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                        backlogItemIds = selectedBacklogItemIds.toList()
                                    )
                                    projectRepository.createSprint(token, projectId, request)
                                    onSuccess()
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error: ${e.message}")
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Sprint")
                    }
                }
            }
        }
    }
}
