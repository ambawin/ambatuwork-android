package win.ambatu.work.feature.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.feature.network.PeerReviewSummaryItemDto
import win.ambatu.work.feature.network.ProjectMemberDto
import win.ambatu.work.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeerReviewActivity : ComponentActivity() {

    private val viewModel: PeerReviewViewModel by viewModels()

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
                PeerReviewScreen(
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
            return Intent(context, PeerReviewActivity::class.java).apply {
                putExtra(EXTRA_PROJECT_ID, projectId)
                putExtra(EXTRA_SPRINT_ID, sprintId)
                putExtra(EXTRA_SPRINT_NAME, sprintName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerReviewScreen(
    viewModel: PeerReviewViewModel,
    sprintName: String,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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

    if (uiState.showReviewForm && uiState.selectedRevieweeId != null) {
        val reviewee = uiState.members.find { it.user.id == uiState.selectedRevieweeId }
        SubmitReviewDialog(
            revieweeName = reviewee?.user?.name ?: "Team Member",
            onDismiss = { viewModel.hideReviewForm() },
            onSubmit = { collab, delivery, comm, continueFb, improveFb ->
                viewModel.submitReview(
                    revieweeUserId = uiState.selectedRevieweeId!!,
                    collaborationScore = collab,
                    deliveryScore = delivery,
                    communicationScore = comm,
                    continueFeedback = continueFb,
                    improveFeedback = improveFb
                )
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
                            text = "Peer Review",
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
                    IconButton(onClick = { viewModel.loadCycle() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.cycle == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YellowAmbatu)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(LightYellowAmbatu),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val cycle = uiState.cycle
                val isAdmin = viewModel.isAdmin()
                val cycleStatus = cycle?.status?.lowercase()

                // Status Card
                item {
                    CycleStatusCard(
                        cycleStatus = cycleStatus,
                        isAdmin = isAdmin,
                        isLoading = uiState.isLoading,
                        onOpenCycle = { viewModel.openCycle() },
                        onCloseCycle = { viewModel.closeCycle() }
                    )
                }

                // If cycle is open → show team members to review
                if (cycleStatus == "open") {
                    item {
                        Text(
                            text = "Review Your Team Members",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkChocoAmbatu
                        )
                    }
                    val reviewableMembers = uiState.members.filter {
                        it.user.id != uiState.currentUserId
                    }
                    items(reviewableMembers, key = { it.user.id ?: 0L }) { member ->
                        ReviewableMemberCard(
                            member = member,
                            onClick = { member.user.id?.let { viewModel.showReviewFormFor(it) } }
                        )
                    }
                }

                // My Summary section
                uiState.mySummary?.let { mySummary ->
                    item {
                        Text(
                            text = "Your Review Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkChocoAmbatu
                        )
                    }
                    item {
                        PersonalSummaryCard(summary = mySummary)
                    }
                }

                // Team Summary (admin only, when closed)
                if (isAdmin && cycleStatus == "closed" && uiState.summary.isNotEmpty()) {
                    item {
                        Text(
                            text = "Team Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkChocoAmbatu
                        )
                    }
                    items(uiState.summary, key = { it.user.id }) { item ->
                        TeamSummaryCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun CycleStatusCard(
    cycleStatus: String?,
    isAdmin: Boolean,
    isLoading: Boolean,
    onOpenCycle: () -> Unit,
    onCloseCycle: () -> Unit
) {
    val (statusText, statusColor, statusBgColor) = when (cycleStatus) {
        "open" -> Triple("Cycle Open — Collecting Reviews", GreenAmbatu, LightGreenAmbatu)
        "closed" -> Triple("Cycle Closed — Results Available", ChocoAmbatu, MediumYellowAmbatu)
        else -> Triple("No Review Cycle Yet", ChocoAmbatu.copy(alpha = 0.6f), LightYellowAmbatu)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBgColor
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isAdmin) {
                Spacer(modifier = Modifier.height(16.dp))
                when (cycleStatus) {
                    null -> {
                        Button(
                            onClick = onOpenCycle,
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenAmbatu,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Open Peer Review Cycle", fontWeight = FontWeight.Bold)
                        }
                    }
                    "open" -> {
                        var showConfirm by remember { mutableStateOf(false) }
                        if (showConfirm) {
                            AlertDialog(
                                onDismissRequest = { showConfirm = false },
                                title = { Text("Close Cycle?", fontWeight = FontWeight.Bold) },
                                text = { Text("Are you sure? Once closed, no more reviews can be submitted. Results will become visible.") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            showConfirm = false
                                            onCloseCycle()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = RedAmbatu,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Close Cycle")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showConfirm = false }) {
                                        Text("Cancel", color = ChocoAmbatu)
                                    }
                                }
                            )
                        }
                        Button(
                            onClick = { showConfirm = true },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedAmbatu,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Close Peer Review Cycle", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewableMemberCard(
    member: ProjectMemberDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            if (member.user.avatarUrl != null) {
                AsyncImage(
                    model = member.user.avatarUrl,
                    contentDescription = member.user.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MediumYellowAmbatu
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = ChocoAmbatu,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.user.name ?: "Unknown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkChocoAmbatu
                )
                Text(
                    text = member.role.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = ChocoAmbatu.copy(alpha = 0.6f)
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowAmbatu,
                    contentColor = DarkChocoAmbatu
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Review", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun PersonalSummaryCard(summary: PeerReviewSummaryItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Reviews received: ${summary.reviewCount}",
                style = MaterialTheme.typography.bodySmall,
                color = ChocoAmbatu.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreCircle("Collab", summary.avgCollaborationScore, BlueAmbatu)
                ScoreCircle("Delivery", summary.avgDeliveryScore, GreenAmbatu)
                ScoreCircle("Comms", summary.avgCommunicationScore, YellowAmbatu)
            }

            if (summary.feedbacks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = LightYellowAmbatu)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Feedback",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkChocoAmbatu
                )
                summary.feedbacks.forEach { fb ->
                    Spacer(modifier = Modifier.height(8.dp))
                    fb.continueFeedback?.let {
                        Row {
                            Text("👍 ", fontSize = 14.sp)
                            Text(it, style = MaterialTheme.typography.bodySmall, color = DarkChocoAmbatu)
                        }
                    }
                    fb.improveFeedback?.let {
                        Row {
                            Text("💡 ", fontSize = 14.sp)
                            Text(it, style = MaterialTheme.typography.bodySmall, color = DarkChocoAmbatu)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreCircle(label: String, score: Double?, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = score?.let { String.format("%.1f", it) } ?: "—",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ChocoAmbatu.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun TeamSummaryCard(item: PeerReviewSummaryItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.user.avatarUrl != null) {
                    AsyncImage(
                        model = item.user.avatarUrl,
                        contentDescription = item.user.name,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MediumYellowAmbatu
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = ChocoAmbatu,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.user.name,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall,
                        color = DarkChocoAmbatu
                    )
                    Text(
                        text = "${item.reviewCount} reviews",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChocoAmbatu.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreCircle("Collab", item.avgCollaborationScore, BlueAmbatu)
                ScoreCircle("Delivery", item.avgDeliveryScore, GreenAmbatu)
                ScoreCircle("Comms", item.avgCommunicationScore, YellowAmbatu)
            }
            if (item.feedbacks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = LightYellowAmbatu)
                Spacer(modifier = Modifier.height(8.dp))
                item.feedbacks.forEach { fb ->
                    fb.continueFeedback?.let {
                        Row {
                            Text("👍 ", fontSize = 13.sp)
                            Text(it, style = MaterialTheme.typography.bodySmall, color = DarkChocoAmbatu)
                        }
                    }
                    fb.improveFeedback?.let {
                        Row {
                            Text("💡 ", fontSize = 13.sp)
                            Text(it, style = MaterialTheme.typography.bodySmall, color = DarkChocoAmbatu)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun SubmitReviewDialog(
    revieweeName: String,
    onDismiss: () -> Unit,
    onSubmit: (Int, Int, Int, String?, String?) -> Unit
) {
    var collaborationScore by remember { mutableIntStateOf(3) }
    var deliveryScore by remember { mutableIntStateOf(3) }
    var communicationScore by remember { mutableIntStateOf(3) }
    var continueFeedback by remember { mutableStateOf("") }
    var improveFeedback by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Review $revieweeName", fontWeight = FontWeight.Bold, color = DarkChocoAmbatu)
                Text(
                    "Rate 1-5 for each category",
                    style = MaterialTheme.typography.labelSmall,
                    color = ChocoAmbatu.copy(alpha = 0.6f)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScoreSlider("🤝 Collaboration", collaborationScore) { collaborationScore = it }
                ScoreSlider("📦 Delivery", deliveryScore) { deliveryScore = it }
                ScoreSlider("💬 Communication", communicationScore) { communicationScore = it }

                HorizontalDivider(color = LightYellowAmbatu)

                OutlinedTextField(
                    value = continueFeedback,
                    onValueChange = { continueFeedback = it },
                    label = { Text("👍 What should they continue?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
                OutlinedTextField(
                    value = improveFeedback,
                    onValueChange = { improveFeedback = it },
                    label = { Text("💡 What could they improve?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        collaborationScore,
                        deliveryScore,
                        communicationScore,
                        continueFeedback.takeIf { it.isNotBlank() },
                        improveFeedback.takeIf { it.isNotBlank() }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowAmbatu,
                    contentColor = DarkChocoAmbatu
                )
            ) {
                Text("Submit Review", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ChocoAmbatu)
            }
        }
    )
}

@Composable
fun ScoreSlider(label: String, score: Int, onScoreChange: (Int) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = DarkChocoAmbatu)
            Text(
                text = "$score/5",
                fontWeight = FontWeight.Bold,
                color = when {
                    score >= 4 -> GreenAmbatu
                    score >= 3 -> YellowAmbatu
                    else -> RedAmbatu
                }
            )
        }
        Slider(
            value = score.toFloat(),
            onValueChange = { onScoreChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = YellowAmbatu,
                activeTrackColor = YellowAmbatu,
                inactiveTrackColor = LightYellowAmbatu
            )
        )
    }
}
