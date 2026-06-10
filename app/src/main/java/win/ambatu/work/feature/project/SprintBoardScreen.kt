package win.ambatu.work.feature.project

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import win.ambatu.work.feature.network.SprintReviewItemRequest
import win.ambatu.work.feature.network.DailyCheckinDto
import win.ambatu.work.R
import coil3.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import win.ambatu.work.feature.network.BacklogItemDto
import win.ambatu.work.ui.theme.BlueAmbatu
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.LightGreenAmbatu
import win.ambatu.work.ui.theme.RedAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu
import win.ambatu.work.ui.theme.DarkChocoAmbatu
import win.ambatu.work.ui.theme.LightYellowAmbatu
import win.ambatu.work.ui.theme.GreenAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import win.ambatu.work.feature.network.SprintBoardDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintBoardScreen(
    viewModel: SprintBoardViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var detailItem by remember { mutableStateOf<BacklogItemDto?>(null) }
    var statusUpdateItem by remember { mutableStateOf<BacklogItemDto?>(null) }
    var showCloseSprintDialog by remember { mutableStateOf(false) }

    if (showCloseSprintDialog && uiState.board != null) {
        CloseSprintDialog(
            board = uiState.board!!,
            onDismiss = { showCloseSprintDialog = false },
            onConfirm = { summary, demoUrl, items ->
                viewModel.submitSprintReviewAndClose(summary, demoUrl, items)
                showCloseSprintDialog = false
            }
        )
    }

    if (uiState.isSprintClosedSuccessfully) {
        SprintClosedSuccessDialog(
            onDismiss = {
                viewModel.resetSprintClosedSuccess()
                onBackClick()
            }
        )
    }

    if (detailItem != null && uiState.project != null) {
        BacklogDetailDialog(
            item = detailItem!!,
            project = uiState.project!!,
            members = uiState.members,
            onDismiss = { detailItem = null },
            onUpdate = { id, title, desc, type, est, priority, ac, assigned ->
                viewModel.updateBacklogItem(id, title, desc, type, est, priority, ac, assigned)
            },
            onArchive = { id ->
                viewModel.archiveBacklogItem(id)
            }
        )
    }

    if (statusUpdateItem != null) {
        UpdateStatusDialog(
            item = statusUpdateItem!!,
            onDismiss = { statusUpdateItem = null },
            onUpdate = { newStatus ->
                viewModel.updateBacklogItemStatus(statusUpdateItem!!.id, newStatus)
                statusUpdateItem = null
            }
        )
    }

    var showDailyStandup by remember { mutableStateOf(false) }

    if (showDailyStandup && uiState.project != null) {
        DailyStandupDialog(
            checkins = uiState.checkins,
            currentUserId = uiState.currentUserId ?: -1L,
            onDismiss = { showDailyStandup = false },
            onSubmitCheckin = { yesterday, today, blockers, confidence, date ->
                viewModel.submitDailyCheckin(yesterday, today, blockers, confidence, date)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.board?.sprint?.name ?: "Sprint Board",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.board?.sprint?.sprintGoal?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = ChocoAmbatu.copy(alpha = 0.8f)
                            )
                        }
                    }
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
                ),
                actions = {
                    val board = uiState.board
                    val role = uiState.currentUserRole?.lowercase()?.trim() ?: ""
                    val isAdmin = role.contains("admin") || 
                                 role.contains("owner") || 
                                 role.contains("master") || 
                                 role.contains("leader")

                    if (board != null && board.sprint.status.lowercase() == "active" && isAdmin) {
                        TextButton(
                            onClick = { showCloseSprintDialog = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Close Sprint", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (board != null && board.sprint.status.lowercase() == "active") {
                        IconButton(onClick = { showDailyStandup = true }) {
                            Icon(Icons.Default.Groups, contentDescription = "Daily Standup")
                        }
                    }

                    IconButton(onClick = { viewModel.loadBoard() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
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
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(onClick = { viewModel.loadBoard() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry")
                    }
                }
            } else {
                val board = uiState.board
                if (board != null) {
                    val sprintStatus = board.sprint.status.lowercase()
                    val role = uiState.currentUserRole?.lowercase()?.trim() ?: ""
                    val isAdmin = role.contains("admin") || 
                                 role.contains("owner") || 
                                 role.contains("master") || 
                                 role.contains("leader")

                    Column(modifier = Modifier.fillMaxSize()) {
                        if (sprintStatus == "planned") {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Sprint is Planned",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "This sprint has not started yet. Ready to kick off?",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                    if (isAdmin) {
                                        Button(
                                            onClick = { viewModel.startSprint() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Text("Start Sprint")
                                        }
                                    }
                                }
                            }
                        }

                        BoardContent(
                            board = board,
                            onItemClick = { item ->
                                val canEdit = isAdmin || item.assignedToUserId == uiState.currentUserId
                                if (canEdit) {
                                    statusUpdateItem = item
                                }
                            },
                            onInfoClick = { detailItem = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoardContent(
    board: SprintBoardDto,
    onItemClick: (BacklogItemDto) -> Unit,
    onInfoClick: (BacklogItemDto) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(scrollState)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BoardColumn(
            title = "Selected",
            items = board.columns.selected,
            onItemClick = onItemClick,
            onInfoClick = onInfoClick,
            headerColor = MaterialTheme.colorScheme.secondaryContainer
        )
        BoardColumn(
            title = "In Progress",
            items = board.columns.inProgress,
            onItemClick = onItemClick,
            onInfoClick = onInfoClick,
            headerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
        BoardColumn(
            title = "In Review",
            items = board.columns.inReview,
            onItemClick = onItemClick,
            onInfoClick = onInfoClick,
            headerColor = MaterialTheme.colorScheme.primaryContainer
        )
        BoardColumn(
            title = "Done",
            items = board.columns.done,
            onItemClick = onItemClick,
            onInfoClick = onInfoClick,
            headerColor = LightGreenAmbatu
        )
    }
}

@Composable
fun BoardColumn(
    title: String,
    items: List<BacklogItemDto>,
    onItemClick: (BacklogItemDto) -> Unit,
    onInfoClick: (BacklogItemDto) -> Unit,
    headerColor: Color
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Surface(
            color = headerColor,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Badge(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(text = items.size.toString())
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(items) { item ->
                BoardItemCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    onInfoClick = { onInfoClick(item) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateStatusDialog(
    item: BacklogItemDto,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("selected", "in_progress", "in_review", "done")
    var selectedStatus by remember { mutableStateOf(item.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = item.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedStatus.replace("_", " ").uppercase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.replace("_", " ").uppercase()) },
                                onClick = {
                                    selectedStatus = status
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onUpdate(selectedStatus) }) {
                Text("Update")
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
fun BoardItemCard(
    item: BacklogItemDto,
    onClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.height(24.dp).width(24.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Detail",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(4.dp)
                        .height(16.dp)
                        .background(
                            color = when (item.type.lowercase()) {
                                "story" -> BlueAmbatu
                                "task" -> ChocoAmbatu.copy(alpha = 0.6f)
                                "bug" -> RedAmbatu
                                else -> YellowAmbatu
                            },
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }

            if (item.estimatePoints != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${item.estimatePoints}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyStandupDialog(
    checkins: List<DailyCheckinDto>,
    currentUserId: Long,
    onDismiss: () -> Unit,
    onSubmitCheckin: (yesterday: String?, today: String?, blockers: String?, confidence: Int, date: String) -> Unit
) {
    var showSubmitDialog by remember { mutableStateOf(false) }
    val todayStr = remember { java.time.LocalDate.now().toString() }

    val hasCheckedInToday = remember(checkins, currentUserId, todayStr) {
        checkins.any { it.user.id == currentUserId && it.checkinDate == todayStr }
    }

    val checkinsByDate = remember(checkins) {
        checkins.groupBy { it.checkinDate }.toSortedMap(compareByDescending { it })
    }

    if (showSubmitDialog) {
        SubmitCheckinDialog(
            onDismiss = { showSubmitDialog = false },
            onSubmit = { yesterday, today, blockers, confidence ->
                onSubmitCheckin(yesterday, today, blockers, confidence, todayStr)
                showSubmitDialog = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daily Standups")
                if (!hasCheckedInToday) {
                    Button(onClick = { showSubmitDialog = true }) {
                        Text("Check in Today")
                    }
                }
            }
        },
        text = {
            if (checkins.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No standup check-ins yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    checkinsByDate.forEach { (date, dailyLogs) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(
                            count = dailyLogs.size,
                            key = { index -> dailyLogs[index].id }
                        ) { index ->
                            val log = dailyLogs[index]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        AsyncImage(
                                            model = log.user.avatarUrl,
                                            contentDescription = "User avatar",
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                            placeholder = painterResource(R.drawable.profile_placeholder),
                                            error = painterResource(R.drawable.profile_placeholder)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = log.user.name ?: "Unknown",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Confidence: ${log.confidenceScore}/5",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = when (log.confidenceScore) {
                                                    5 -> LightGreenAmbatu
                                                    4 -> LightGreenAmbatu.copy(alpha = 0.8f)
                                                    3 -> YellowAmbatu
                                                    else -> RedAmbatu
                                                },
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    log.yesterday?.let {
                                        if (it.isNotBlank()) {
                                            Text(
                                                text = "Yesterday",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(bottom = 6.dp)
                                            )
                                        }
                                    }
                                    log.today?.let {
                                        if (it.isNotBlank()) {
                                            Text(
                                                text = "Today",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(bottom = 6.dp)
                                            )
                                        }
                                    }
                                    log.blockers?.let {
                                        if (it.isNotBlank()) {
                                            Text(
                                                text = "Blockers",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.error,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun SubmitCheckinDialog(
    onDismiss: () -> Unit,
    onSubmit: (yesterday: String?, today: String?, blockers: String?, confidence: Int) -> Unit
) {
    var yesterday by remember { mutableStateOf("") }
    var today by remember { mutableStateOf("") }
    var blockers by remember { mutableStateOf("") }
    var confidenceScore by remember { mutableStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Check-in") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = yesterday,
                        onValueChange = { yesterday = it },
                        label = { Text("What did you do yesterday?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                item {
                    OutlinedTextField(
                        value = today,
                        onValueChange = { today = it },
                        label = { Text("What will you do today?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                item {
                    OutlinedTextField(
                        value = blockers,
                        onValueChange = { blockers = it },
                        label = { Text("Any blockers/impediments? (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    if (blockers.isNotBlank()) {
                        Text(
                            text = "Notice: Submitting blockers will automatically create an impediment for the project.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "How confident are you about the sprint goal today?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..5).forEach { score ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { confidenceScore = score }
                                ) {
                                    RadioButton(
                                        selected = (confidenceScore == score),
                                        onClick = { confidenceScore = score }
                                    )
                                    Text(text = "$score", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        yesterday.takeIf { it.isNotBlank() },
                        today.takeIf { it.isNotBlank() },
                        blockers.takeIf { it.isNotBlank() },
                        confidenceScore
                    )
                }
            ) {
                Text("Submit Check-in")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloseSprintDialog(
    board: SprintBoardDto,
    onDismiss: () -> Unit,
    onConfirm: (summary: String, demoUrl: String?, items: List<SprintReviewItemRequest>) -> Unit
) {
    var summary by remember { mutableStateOf("") }
    var demoUrl by remember { mutableStateOf("") }

    val allItems = remember(board) {
        board.columns.selected + board.columns.inProgress + board.columns.inReview + board.columns.done
    }

    val itemDecisions = remember(allItems) {
        mutableStateMapOf<Long, String>().apply {
            allItems.forEach { item ->
                val isDone = board.columns.done.any { it.id == item.id }
                put(item.id, if (isDone) "accepted" else "carry_over")
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Close Sprint & Submit Review") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Sprint Review Summary") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
                item {
                    OutlinedTextField(
                        value = demoUrl,
                        onValueChange = { demoUrl = it },
                        label = { Text("Demo URL (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                    )
                }
                item {
                    Text(
                        text = "Backlog Items Review",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(
                    count = allItems.size,
                    key = { index -> allItems[index].id }
                ) { index ->
                    val item = allItems[index]
                    val currentDecision = itemDecisions[item.id] ?: "carry_over"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Status: ${item.status.uppercase()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
                                            onClick = { itemDecisions[item.id] = value }
                                        )
                                        Text(text = label, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (summary.isNotBlank()) {
                        val requests = itemDecisions.map { (id, decision) ->
                            SprintReviewItemRequest(backlogItemId = id, decision = decision)
                        }
                        onConfirm(summary, demoUrl.takeIf { it.isNotBlank() }, requests)
                    }
                },
                enabled = summary.isNotBlank()
            ) {
                Text("Submit and Close")
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
fun SprintClosedSuccessDialog(
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteAmbatu),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebration Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(LightYellowAmbatu, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = GreenAmbatu,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sprint Closed Successfully! 🎉",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkChocoAmbatu,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = LightYellowAmbatu, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Under the Hood Section
                Text(
                    text = "What happened under the hood:",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkChocoAmbatu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BulletPoint("Sprint Review Submitted", "Your sprint review has been saved in the database.", GreenAmbatu)
                    BulletPoint("Backlog Items Updated", "Done items are completed. Unfinished items are moved back to ready in the backlog.", BlueAmbatu)
                    BulletPoint("Sprint Finalized", "The sprint status is officially changed to closed.", DarkChocoAmbatu)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Next Steps Section
                Text(
                    text = "Suggested Next Steps (Scrum Flow):",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkChocoAmbatu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BulletPoint("Sprint Retrospective", "Set team happiness score and create retro items (went well, to improve).", ChocoAmbatu)
                    BulletPoint("Peer Review Cycle", "Initiate and participate in peer evaluations.", ChocoAmbatu)
                    BulletPoint("Plan Next Sprint", "Create and start the next sprint to continue development.", ChocoAmbatu)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = YellowAmbatu),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Back to Project Details",
                        color = DarkChocoAmbatu,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BulletPoint(
    title: String,
    desc: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(8.dp)
                .background(iconColor, CircleShape)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = DarkChocoAmbatu
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = ChocoAmbatu
            )
        }
    }
}
