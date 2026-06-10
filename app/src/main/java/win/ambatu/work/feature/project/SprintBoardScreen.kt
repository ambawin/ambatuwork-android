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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
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

    if (detailItem != null && uiState.project != null) {
        BacklogDetailDialog(
            item = detailItem!!,
            project = uiState.project!!,
            members = uiState.members,
            onDismiss = { detailItem = null },
            onUpdate = { id, title, desc, type, est, bv, ac, assigned ->
                viewModel.updateBacklogItem(id, title, desc, type, est, bv, ac, assigned)
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
                            style = MaterialTheme.typography.titleMedium
                        )
                        uiState.board?.sprint?.sprintGoal?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
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
                    BoardContent(
                        board = board,
                        onItemClick = { item ->
                            val role = uiState.currentUserRole?.lowercase()?.trim() ?: ""
                            val isAdmin = role.contains("admin") || 
                                         role.contains("owner") || 
                                         role.contains("master") || 
                                         role.contains("leader")
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
