package win.ambatu.work.feature.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import win.ambatu.work.feature.network.ProjectMemberDto
import win.ambatu.work.feature.network.RetroItemDto
import win.ambatu.work.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import win.ambatu.work.ui.components.CircularFloatingActionButton

@AndroidEntryPoint
class RetrospectiveActivity : ComponentActivity() {

    private val viewModel: RetrospectiveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val projectId = intent.getLongExtra(EXTRA_PROJECT_ID, -1L)
        val sprintId = intent.getLongExtra(EXTRA_SPRINT_ID, -1L)
        if (projectId == -1L || sprintId == -1L) {
            finish()
            return
        }

        setContent {
            AmbatuWorkTheme {
                RetrospectiveScreen(
                    viewModel = viewModel,
                    sprintName = intent.getStringExtra(EXTRA_SPRINT_NAME) ?: "Sprint",
                    onBackClick = { finish() }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_PROJECT_ID = "extra_project_id"
        private const val EXTRA_SPRINT_ID = "extra_sprint_id"
        private const val EXTRA_SPRINT_NAME = "extra_sprint_name"

        fun createIntent(
            context: Context,
            projectId: Long,
            sprintId: Long,
            sprintName: String
        ): Intent {
            return Intent(context, RetrospectiveActivity::class.java).apply {
                putExtra(EXTRA_PROJECT_ID, projectId)
                putExtra(EXTRA_SPRINT_ID, sprintId)
                putExtra(EXTRA_SPRINT_NAME, sprintName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetrospectiveScreen(
    viewModel: RetrospectiveViewModel,
    sprintName: String,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    if (showAddDialog) {
        AddFeedbackDialog(
            tabTypes = listOf("went_well", "problem", "action"),
            selectedTab = uiState.selectedTab,
            members = uiState.members,
            onDismiss = { showAddDialog = false },
            onSubmit = { type, body, assignedTo ->
                viewModel.addFeedbackItem(type, body, assignedTo)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Retrospective",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sprintName,
                            style = MaterialTheme.typography.labelSmall,
                            color = ChocoAmbatu.copy(alpha = 0.7f)
                        )
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
                    IconButton(onClick = { viewModel.loadRetrospective() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            CircularFloatingActionButton(
                onClick = { showAddDialog = true },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Feedback")
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.retrospective == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ChocoAmbatu)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(YellowAmbatu),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Happiness Score Section
                item {
                    HappinessScoreCard(
                        currentScore = uiState.retrospective?.teamHappinessScore,
                        onScoreSelected = { viewModel.submitHappinessScore(it) },
                        isLoading = uiState.isLoading
                    )
                }

                // Tab Bar
                item {
                    FeedbackTabBar(
                        selectedTab = uiState.selectedTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        retroItems = uiState.retrospective?.items ?: emptyList()
                    )
                }

                // Feedback Items
                val tabType = when (uiState.selectedTab) {
                    0 -> "went_well"
                    1 -> "problem"
                    2 -> "action"
                    else -> "went_well"
                }
                val filteredItems = (uiState.retrospective?.items ?: emptyList())
                    .filter { it.type == tabType }

                if (filteredItems.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WhiteAmbatu),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No feedback items yet. Tap + to add feedback.",
                                    color = ChocoAmbatu.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WhiteAmbatu),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                filteredItems.forEachIndexed { index, item ->
                                    RetroFeedbackRow(
                                        item = item,
                                        currentUserId = uiState.currentUserId,
                                        onDelete = { viewModel.deleteFeedbackItem(item.id) }
                                    )
                                    if (index < filteredItems.lastIndex) {
                                        HorizontalDivider(
                                            color = ChocoAmbatu.copy(alpha = 0.15f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HappinessScoreCard(
    currentScore: Int?,
    onScoreSelected: (Int) -> Unit,
    isLoading: Boolean
) {
    val emojis = listOf("😢", "😕", "😐", "🙂", "😄")
    val labels = listOf("Very Sad", "Sad", "Neutral", "Happy", "Very Happy")
    var selectedScore by remember(currentScore) { mutableIntStateOf(currentScore ?: 0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WhiteAmbatu),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Team Happiness",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ChocoAmbatu
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentScore != null) "${labels[currentScore - 1]}"
                       else "How did the team feel about this sprint?",
                style = MaterialTheme.typography.bodySmall,
                color = ChocoAmbatu.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                emojis.forEachIndexed { index, emoji ->
                    val score = index + 1
                    val isSelected = selectedScore == score
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.3f else 1.0f,
                        label = "emoji_scale"
                    )
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) YellowAmbatu.copy(alpha = 0.2f) else Color.Transparent,
                        label = "emoji_bg"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .clickable {
                                selectedScore = score
                                onScoreSelected(score)
                            }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 28.sp,
                            modifier = Modifier.scale(scale)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) ChocoAmbatu else ChocoAmbatu.copy(alpha = 0.5f),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    retroItems: List<RetroItemDto>
) {
    val tabTitles = listOf("✅ Went Well", "⚠️ Problems", "🎯 Actions")
    val tabTypes = listOf("went_well", "problem", "action")

    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = ChocoAmbatu,
        contentColor = WhiteAmbatu,
        indicator = {},
        divider = {},
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
    ) {
        tabTitles.forEachIndexed { index, title ->
            val count = retroItems.count { it.type == tabTypes[index] }
            val isSelected = selectedTab == index

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .background(if (isSelected) WhiteAmbatu else ChocoAmbatu)
                    .clip(RoundedCornerShape(12.dp)),
                text = {
                    Text(
                        text = if (count > 0) "$title ($count)" else title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) ChocoAmbatu else WhiteAmbatu,
                        maxLines = 1
                    )
                }
            )
        }
    }
}

@Composable
fun RetroFeedbackRow(
    item: RetroItemDto,
    currentUserId: Long?,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Type badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = YellowAmbatu.copy(alpha = 0.2f)
            ) {
                Text(
                    text = (item.type ?: "item").replace("_", " ").replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = ChocoAmbatu,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Author name
            item.author?.let { author ->
                Text(
                    text = author.name ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = ChocoAmbatu.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // Delete button (only for own items)
            if (currentUserId != null && item.authorUserId == currentUserId) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = RedAmbatu,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.body ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = ChocoAmbatu,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeedbackDialog(
    tabTypes: List<String>,
    selectedTab: Int,
    members: List<ProjectMemberDto>,
    onDismiss: () -> Unit,
    onSubmit: (type: String, body: String, assignedToUserId: Long?) -> Unit
) {
    var feedbackBody by remember { mutableStateOf("") }
    var selectedType by remember { mutableIntStateOf(selectedTab) }
    var selectedMemberId by remember { mutableStateOf<Long?>(null) }
    var memberDropdownExpanded by remember { mutableStateOf(false) }

    val typeLabels = listOf("✅ Went Well", "⚠️ Problem", "🎯 Action Item")
    val typeValues = listOf("went_well", "problem", "action")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Feedback", fontWeight = FontWeight.Bold, color = ChocoAmbatu)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Category selector
                Text("Category", style = MaterialTheme.typography.labelMedium, color = ChocoAmbatu)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    typeLabels.forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = selectedType == index,
                            onClick = { selectedType = index },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = typeLabels.size
                            ),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = ChocoAmbatu,
                                activeContentColor = WhiteAmbatu,
                                inactiveContainerColor = WhiteAmbatu,
                                inactiveContentColor = ChocoAmbatu
                            ),
                            label = {
                                Text(label, fontSize = 11.sp, maxLines = 1)
                            }
                        )
                    }
                }

                // Feedback body
                OutlinedTextField(
                    value = feedbackBody,
                    onValueChange = { feedbackBody = it },
                    label = { Text("Feedback") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChocoAmbatu,
                        unfocusedBorderColor = ChocoAmbatu.copy(alpha = 0.5f),
                        focusedLabelColor = ChocoAmbatu,
                        cursorColor = ChocoAmbatu
                    )
                )

                // Assign to member (for Action Items)
                if (selectedType == 2) {
                    Text("Assign to (optional)", style = MaterialTheme.typography.labelMedium, color = ChocoAmbatu)
                    ExposedDropdownMenuBox(
                        expanded = memberDropdownExpanded,
                        onExpandedChange = { memberDropdownExpanded = !memberDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = members.find { it.user.id == selectedMemberId }?.user?.name ?: "None",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = memberDropdownExpanded,
                            onDismissRequest = { memberDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = {
                                    selectedMemberId = null
                                    memberDropdownExpanded = false
                                }
                            )
                            members.forEach { member ->
                                DropdownMenuItem(
                                    text = { Text(member.user.name ?: "Unknown") },
                                    onClick = {
                                        selectedMemberId = member.user.id
                                        memberDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (feedbackBody.isNotBlank()) {
                        onSubmit(
                            typeValues[selectedType],
                            feedbackBody.trim(),
                            if (selectedType == 2) selectedMemberId else null
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocoAmbatu,
                    contentColor = WhiteAmbatu
                ),
                enabled = feedbackBody.isNotBlank()
            ) {
                Text("Submit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ChocoAmbatu)
            }
        }
    )
}
