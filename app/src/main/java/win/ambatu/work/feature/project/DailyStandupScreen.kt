package win.ambatu.work.feature.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.RedAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyStandupScreen(
    viewModel: DailyStandupViewModel,
    onBackClick: () -> Unit,
    onCheckinClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val todayStr = remember { java.time.LocalDate.now().toString() }

    val hasCheckedInToday = remember(uiState.checkins, uiState.currentUserId, todayStr) {
        val currentUserId = uiState.currentUserId ?: return@remember false
        uiState.checkins.any { it.user.id == currentUserId && it.checkinDate == todayStr }
    }

    val checkinsByDate = remember(uiState.checkins) {
        uiState.checkins.groupBy { it.checkinDate }.toSortedMap(compareByDescending { it })
    }

    Scaffold(
        containerColor = YellowAmbatu,
        topBar = {
            TopAppBar(
                title = { Text("Daily Standups", fontWeight = FontWeight.Bold) },
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
                    IconButton(onClick = { viewModel.loadCheckins() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Check-in header prompt/action
            if (!hasCheckedInToday && uiState.currentUserId != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteAmbatu),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You haven't checked in today",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ChocoAmbatu
                        )
                        Button(
                            onClick = onCheckinClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChocoAmbatu,
                                contentColor = WhiteAmbatu
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Check in Today")
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ChocoAmbatu)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
            } else if (uiState.checkins.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = ChocoAmbatu.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No standup check-ins yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ChocoAmbatu.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    checkinsByDate.forEach { (date, dailyLogs) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChocoAmbatu,
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
                                    containerColor = WhiteAmbatu
                                ),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (log.user.avatarUrl != null) {
                                            AsyncImage(
                                                model = log.user.avatarUrl,
                                                contentDescription = "User avatar",
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Surface(
                                                modifier = Modifier.size(40.dp),
                                                shape = CircleShape,
                                                color = YellowAmbatu.copy(alpha = 0.2f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.profile_placeholder),
                                                        contentDescription = null,
                                                        tint = ChocoAmbatu,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = log.user.name ?: "Unknown",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = ChocoAmbatu
                                            )
                                            Text(
                                                text = "Confidence: ${log.confidenceScore}/5",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = when (log.confidenceScore) {
                                                    5 -> ChocoAmbatu
                                                    4 -> ChocoAmbatu.copy(alpha = 0.8f)
                                                    3 -> ChocoAmbatu.copy(alpha = 0.6f)
                                                    else -> RedAmbatu
                                                },
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    log.yesterday?.let {
                                        if (it.isNotBlank()) {
                                            Text(
                                                text = "Yesterday",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = ChocoAmbatu.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ChocoAmbatu,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        }
                                    }
                                    log.today?.let {
                                        if (it.isNotBlank()) {
                                            Text(
                                                text = "Today",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = ChocoAmbatu.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ChocoAmbatu,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        }
                                    }
                                    log.blockers?.let {
                                        if (it.isNotBlank()) {
                                            Text(
                                                text = "Blockers",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = RedAmbatu
                                            )
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = RedAmbatu,
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
        }
    }
}
