package win.ambatu.work.feature.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import win.ambatu.work.feature.network.SprintReviewItemRequest
import win.ambatu.work.ui.components.AmbatuTextField
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloseSprintScreen(
    viewModel: CloseSprintViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var summary by remember { mutableStateOf("") }
    var demoUrl by remember { mutableStateOf("") }
    
    val board = uiState.board
    val allItems = remember(board) {
        if (board != null) {
            board.columns.selected + board.columns.inProgress + board.columns.inReview + board.columns.done
        } else {
            emptyList()
        }
    }

    val itemDecisions = remember(allItems) {
        mutableStateMapOf<Long, String>().apply {
            allItems.forEach { item ->
                val isDone = board?.columns?.done?.any { it.id == item.id } == true
                put(item.id, if (isDone) "accepted" else "carry_over")
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        containerColor = YellowAmbatu,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Close Sprint & Submit Review", fontWeight = FontWeight.Bold) },
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
        if (uiState.isLoading) {
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
                    value = summary,
                    onValueChange = { summary = it },
                    label = "Sprint Review Summary",
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                AmbatuTextField(
                    value = demoUrl,
                    onValueChange = { demoUrl = it },
                    label = "Demo URL (Optional)",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true
                )

                if (allItems.isNotEmpty()) {
                    Text(
                        text = "Backlog Items Review",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChocoAmbatu,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    allItems.forEach { item ->
                        val currentDecision = itemDecisions[item.id] ?: "carry_over"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = WhiteAmbatu
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ChocoAmbatu
                                )
                                Text(
                                    text = "Status: ${item.status.uppercase()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ChocoAmbatu.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    listOf("accepted" to "Accept", "carry_over" to "Carry Over", "rejected" to "Reject").forEach { (value, label) ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { itemDecisions[item.id] = value }
                                        ) {
                                            RadioButton(
                                                selected = (currentDecision == value),
                                                onClick = { itemDecisions[item.id] = value },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = ChocoAmbatu,
                                                    unselectedColor = ChocoAmbatu.copy(alpha = 0.6f)
                                                )
                                            )
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ChocoAmbatu,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (summary.isNotBlank()) {
                            val requests = itemDecisions.map { (id, decision) ->
                                SprintReviewItemRequest(backlogItemId = id, decision = decision)
                            }
                            viewModel.closeSprint(
                                summary = summary.trim(),
                                demoUrl = demoUrl.trim().takeIf { it.isNotBlank() },
                                items = requests
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChocoAmbatu,
                        contentColor = WhiteAmbatu,
                        disabledContainerColor = ChocoAmbatu.copy(alpha = 0.5f),
                        disabledContentColor = WhiteAmbatu.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSubmitting && summary.isNotBlank()
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WhiteAmbatu,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Submit and Close")
                    }
                }
            }
        }
    }
}
