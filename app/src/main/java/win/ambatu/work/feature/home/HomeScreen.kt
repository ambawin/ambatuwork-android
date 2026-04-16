package win.ambatu.work.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import win.ambatu.work.R
import win.ambatu.work.ui.theme.AmbatuWorkTheme
import java.time.LocalTime

@Composable
fun HomeScreen(
    onProfileIconClick: () -> Unit = {}
) {
    val hour = LocalTime.now().hour

    val greeting = when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Content(
        greetingText = greeting,
        onProfileIconClick = onProfileIconClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    greetingText: String = "Good Morning",
    onProfileIconClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "AmbatuWork",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onProfileIconClick
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_placeholder),
                            contentDescription = "User profile picture",
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            item {
                Column(

                ) {
                    Text(
                        text = "${greetingText},",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Perrell Brown",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(16.dp)
                )
            }

            // Ranking Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PointsCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        text = "Points Earned",
                        number = 6767,
                    )

                    PointsCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        text = "Rank in AmbatuWork",
                        number = 1,
                        prefix = "#"
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(24.dp)
                )
            }

            // Today's Focus
            item {
                // Title
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Today's Focus",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }

            // Lists
            items(10) {
                FocusCard(
                    focusText = "Backend",
                    focusDue = "Due Today",
                    focusTeam = "Amba",
                    point = 67
                )
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }

            item {
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }

            // Today's Focus
            item {
                // Title
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Pending Actions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
            }
        }
    }
}

@Composable
private fun PointsCard(
    modifier: Modifier = Modifier,
    text: String = "Points",
    number: Int = 0,
    prefix: String = ""
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = prefix + number.toString(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FocusCard(
    focusText: String,
    focusDue: String,
    focusTeam: String,
    point: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = "Focus Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(32.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = focusText,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$focusDue - $focusTeam",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "+ ${point}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Pts")
            }
        }
    }
}

@Composable
@Preview(group = "Home")
private fun HomePreview() {
    AmbatuWorkTheme {
        Content()
    }
}