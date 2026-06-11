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
import win.ambatu.work.ui.theme.RedAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu
import win.ambatu.work.ui.theme.DarkChocoAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu
import win.ambatu.work.ui.theme.SecondaryYellowAmbatu
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import win.ambatu.work.feature.network.SprintBoardDto
import androidx.compose.ui.unit.sp

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
                // Stay on screen - the board will now show PostSprintDashboard
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
                        val context = androidx.compose.ui.platform.LocalContext.current
                        IconButton(onClick = {
                            context.startActivity(
                                DailyStandupActivity.createIntent(context, board.sprint.projectId, board.sprint.id)
                            )
                        }) {
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
                                    containerColor = WhiteAmbatu
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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

                        if (sprintStatus == "closed") {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val sprintName = board.sprint.name
                            val pId = board.sprint.let { uiState.project?.id ?: -1L }
                            val sId = board.sprint.id

                            PostSprintDashboard(
                                uiState = uiState,
                                isAdmin = isAdmin,
                                onOpenRetrospective = {
                                    context.startActivity(
                                        RetrospectiveActivity.createIntent(
                                            context, pId, sId, sprintName
                                        )
                                    )
                                },
                                onOpenPeerReview = {
                                    context.startActivity(
                                        PeerReviewActivity.createIntent(
                                            context, pId, sId, sprintName
                                        )
                                    )
                                },
                                onOpenCycle = { viewModel.openPeerReviewCycle() }
                            )
                        } else {
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
}

@Composable
fun PostSprintDashboard(
    uiState: SprintBoardUiState,
    isAdmin: Boolean,
    onOpenRetrospective: () -> Unit,
    onOpenPeerReview: () -> Unit,
    onOpenCycle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YellowAmbatu)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏁",
                    fontSize = 36.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sprint Completed!",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = ChocoAmbatu
                )
                Text(
                    text = "Time to reflect and review with your team.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ChocoAmbatu.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Retrospective Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WhiteAmbatu),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            onClick = onOpenRetrospective
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = YellowAmbatu.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("💭", fontSize = 26.sp)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sprint Retrospective",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocoAmbatu
                    )
                    val retroStatus = if (uiState.retroExists) {
                        val score = uiState.retrospective?.teamHappinessScore
                        val itemCount = uiState.retrospective?.items?.size ?: 0
                        if (score != null) "Score: ${listOf("😢","😕","😐","🙂","😄")[score-1]} · $itemCount items"
                        else "$itemCount items added"
                    } else {
                        "Start your team retrospective"
                    }
                    Text(
                        text = retroStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocoAmbatu.copy(alpha = 0.7f)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (uiState.retroExists) YellowAmbatu else YellowAmbatu.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (uiState.retroExists) "View" else "Start",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChocoAmbatu
                    )
                }
            }
        }

        // Peer Review Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WhiteAmbatu),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            onClick = {
                if (uiState.cycleExists) {
                    onOpenPeerReview()
                } else if (isAdmin) {
                    onOpenCycle()
                }
            }
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = YellowAmbatu.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("⭐", fontSize = 26.sp)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Peer Review",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkChocoAmbatu
                    )
                    val cycleStatus = uiState.peerReviewCycle?.status?.lowercase()
                    val peerReviewStatus = when (cycleStatus) {
                        "open" -> "Cycle open — Submit your reviews"
                        "closed" -> "Cycle closed — View results"
                        else -> if (isAdmin) "Open a review cycle for this sprint" else "Waiting for admin to open cycle"
                    }
                    Text(
                        text = peerReviewStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocoAmbatu.copy(alpha = 0.7f)
                    )
                }

                val cycleStatus = uiState.peerReviewCycle?.status?.lowercase()
                when {
                    cycleStatus == "open" -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = YellowAmbatu.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "Review",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChocoAmbatu
                            )
                        }
                    }
                    cycleStatus == "closed" -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ChocoAmbatu.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Results",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChocoAmbatu
                            )
                        }
                    }
                    isAdmin -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = YellowAmbatu
                        ) {
                            Text(
                                text = "Open",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChocoAmbatu
                            )
                        }
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = YellowAmbatu.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Pending",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChocoAmbatu.copy(alpha = 0.5f)
                            )
                        }
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
            headerColor = YellowAmbatu.copy(alpha = 0.15f)
        )
        BoardColumn(
            title = "In Progress",
            items = board.columns.inProgress,
            onItemClick = onItemClick,
            onInfoClick = onInfoClick,
            headerColor = YellowAmbatu.copy(alpha = 0.35f)
        )
        BoardColumn(
            title = "In Review",
            items = board.columns.inReview,
            onItemClick = onItemClick,
            onInfoClick = onInfoClick,
            headerColor = YellowAmbatu.copy(alpha = 0.55f)
        )
        BoardColumn(
            title = "Done",
            items = board.columns.done,
            onItemClick = onItemClick,
            onInfoClick = onInfoClick,
            headerColor = YellowAmbatu
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
                color = WhiteAmbatu.copy(alpha = 0.6f),
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
                    fontWeight = FontWeight.Bold,
                    color = ChocoAmbatu
                )
                Badge(
                    containerColor = ChocoAmbatu.copy(alpha = 0.1f),
                    contentColor = ChocoAmbatu
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
            containerColor = WhiteAmbatu
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                            containerColor = WhiteAmbatu
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                        .background(YellowAmbatu.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = ChocoAmbatu,
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
                HorizontalDivider(color = ChocoAmbatu.copy(alpha = 0.15f), thickness = 1.dp)
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
                    BulletPoint("Sprint Review Submitted", "Your sprint review has been saved in the database.", ChocoAmbatu)
                    BulletPoint("Backlog Items Updated", "Done items are completed. Unfinished items are moved back to ready in the backlog.", ChocoAmbatu)
                    BulletPoint("Sprint Finalized", "The sprint status is officially changed to closed.", ChocoAmbatu)
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
