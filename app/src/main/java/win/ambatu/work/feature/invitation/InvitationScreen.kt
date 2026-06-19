package win.ambatu.work.feature.invitation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import win.ambatu.work.ui.theme.YellowAmbatu
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu
import win.ambatu.work.ui.theme.SecondaryYellowAmbatu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import win.ambatu.work.R
import win.ambatu.work.feature.network.ProjectInvitationDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationScreen(
    viewModel: InvitationViewModel,
    onInvitationAccepted: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.acceptSuccess) {
        if (uiState.acceptSuccess) {
            onInvitationAccepted()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        containerColor = YellowAmbatu,
        topBar = {
            TopAppBar(
                title = { Text("Invitations", fontWeight = FontWeight.Bold) },
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ChocoAmbatu
                )
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = ChocoAmbatu,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.invitations.isEmpty()) {
                Text(
                    text = "No invitations found",
                    color = ChocoAmbatu,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.invitations) { invitation ->
                        InvitationItem(
                            invitation = invitation,
                            onClick = {
                                invitation.token?.let { viewModel.acceptInvitation(it) }
                            }
                        )
                    }
                }
            }

            if (uiState.isAccepting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ChocoAmbatu)
                }
            }
        }
    }
}

@Composable
fun InvitationItem(
    invitation: ProjectInvitationDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteAmbatu),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AsyncImage(
                model = invitation.project?.owner?.avatarUrl,
                placeholder = painterResource(id = R.drawable.profile_placeholder),
                error = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = "Project Owner Avatar",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = invitation.project?.name ?: "Unnamed Project",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    color = ChocoAmbatu,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Invited by: ${invitation.project?.owner?.name ?: "Unknown"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ChocoAmbatu.copy(alpha = 0.8f)
                )
                invitation.project?.owner?.email?.let { email ->
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9CA3AF)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                
                val roleBadgeText = invitation.role.lowercase().split(" ").joinToString(" ") { word -> 
                    word.replaceFirstChar { it.uppercase() } 
                }
                
                val badgeBgColor = when (invitation.role.lowercase().trim()) {
                    "owner" -> ChocoAmbatu
                    "supervisor" -> SecondaryYellowAmbatu
                    else -> YellowAmbatu
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = badgeBgColor
                ) {
                    Text(
                        text = roleBadgeText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = WhiteAmbatu,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
