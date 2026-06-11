package win.ambatu.work.feature.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import win.ambatu.work.data.repository.ProjectRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.network.SubmitDailyCheckinRequest
import win.ambatu.work.ui.components.AmbatuTextField
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDailyCheckinScreen(
    projectId: Long,
    sprintId: Long,
    projectRepository: ProjectRepository,
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    var yesterday by remember { mutableStateOf("") }
    var today by remember { mutableStateOf("") }
    var blockers by remember { mutableStateOf("") }
    var confidenceScore by remember { mutableStateOf(5) }
    
    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val todayStr = remember { java.time.LocalDate.now().toString() }

    Scaffold(
        containerColor = YellowAmbatu,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Daily Check-in", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AmbatuTextField(
                value = yesterday,
                onValueChange = { yesterday = it },
                label = "What did you do yesterday?",
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            AmbatuTextField(
                value = today,
                onValueChange = { today = it },
                label = "What will you do today?",
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                AmbatuTextField(
                    value = blockers,
                    onValueChange = { blockers = it },
                    label = "Any blockers/impediments? (Optional)",
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                if (blockers.isNotBlank()) {
                    Text(
                        text = "Notice: Submitting blockers will automatically create an impediment for the project.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "How confident are you about the sprint goal today?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChocoAmbatu,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { score ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { confidenceScore = score }
                        ) {
                            RadioButton(
                                selected = (confidenceScore == score),
                                onClick = { confidenceScore = score },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = ChocoAmbatu,
                                    unselectedColor = ChocoAmbatu.copy(alpha = 0.6f)
                                )
                            )
                            Text(
                                text = "$score",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ChocoAmbatu,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isSubmitting = true
                    scope.launch {
                        try {
                            val token = sessionManager.getToken()
                            if (token != null) {
                                val request = SubmitDailyCheckinRequest(
                                    yesterday = yesterday.takeIf { it.isNotBlank() },
                                    today = today.takeIf { it.isNotBlank() },
                                    blockers = blockers.takeIf { it.isNotBlank() },
                                    confidenceScore = confidenceScore,
                                    checkinDate = todayStr
                                )
                                projectRepository.submitDailyCheckin(token, projectId, sprintId, request)
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
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = WhiteAmbatu,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Submit Check-in")
                }
            }
        }
    }
}
