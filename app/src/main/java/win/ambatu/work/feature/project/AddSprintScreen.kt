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
import win.ambatu.work.feature.network.ProjectDto
import win.ambatu.work.ui.components.AmbatuTextField
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu
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
    var defaultSprintLengthDays by remember { mutableStateOf(14) }
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

                // Fetch project details to get default sprint length
                val project = projectRepository.getProject(token, projectId)
                defaultSprintLengthDays = project.defaultSprintLengthDays ?: 14
            }
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Failed to load screen data: ${e.message}")
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
        containerColor = YellowAmbatu,
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
                    value = name,
                    onValueChange = { if (it.length <= 255) name = it },
                    label = "Sprint Name",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("${name.length}/255", color = ChocoAmbatu.copy(alpha = 0.8f)) }
                )

                AmbatuTextField(
                    value = sprintGoal,
                    onValueChange = { if (it.length <= 5000) sprintGoal = it },
                    label = "Sprint Goal",
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    supportingText = { Text("${sprintGoal.length}/5000", color = ChocoAmbatu.copy(alpha = 0.8f)) }
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
                    fontWeight = FontWeight.Bold,
                    color = ChocoAmbatu
                )

                if (backlogItems.isEmpty()) {
                    Text(
                        text = "No available backlog items to add to sprint.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChocoAmbatu.copy(alpha = 0.8f)
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = WhiteAmbatu
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(backlogItems) { item ->
                                val isSelected = selectedBacklogItemIds.contains(item.id)
                                ListItem(
                                    headlineContent = { Text(item.title, color = ChocoAmbatu, fontWeight = FontWeight.SemiBold) },
                                    supportingContent = { Text("${item.type.uppercase()} • ${item.estimatePoints ?: 0} pts", color = ChocoAmbatu.copy(alpha = 0.7f)) },
                                    trailingContent = {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                selectedBacklogItemIds = if (checked) {
                                                    selectedBacklogItemIds + item.id
                                                } else {
                                                    selectedBacklogItemIds - item.id
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = ChocoAmbatu,
                                                uncheckedColor = ChocoAmbatu.copy(alpha = 0.6f),
                                                checkmarkColor = YellowAmbatu
                                            )
                                        )
                                    },
                                    colors = ListItemDefaults.colors(
                                        containerColor = Color.Transparent,
                                        headlineColor = ChocoAmbatu,
                                        supportingColor = ChocoAmbatu.copy(alpha = 0.7f)
                                    ),
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
                            (endDate!!.toEpochDay() - startDate!!.toEpochDay()) > defaultSprintLengthDays ->
                                "The sprint duration cannot exceed the project limit of $defaultSprintLengthDays days."
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
                            } catch (e: retrofit2.HttpException) {
                                val errorBody = e.response()?.errorBody()?.string()
                                val errorMsg = if (!errorBody.isNullOrBlank()) {
                                    try {
                                        val jsonObject = org.json.JSONObject(errorBody)
                                        jsonObject.optString("message", e.message())
                                    } catch (jsonEx: Exception) {
                                        "Error: ${e.message()}"
                                    }
                                } else {
                                    "Error: ${e.message()}"
                                }
                                snackbarHostState.showSnackbar(errorMsg)
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
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WhiteAmbatu,
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
