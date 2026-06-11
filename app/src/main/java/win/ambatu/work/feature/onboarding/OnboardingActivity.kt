package win.ambatu.work.feature.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import win.ambatu.work.R
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.auth.LoginActivity
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.DarkChocoAmbatu
import win.ambatu.work.ui.theme.MontserratFamily
import win.ambatu.work.ui.theme.WhiteAmbatu
import win.ambatu.work.ui.theme.YellowAmbatu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnboardingScreen(
                onFinish = {
                    sessionManager.setCompletedOnboarding(true)
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}

data class OnboardingPageData(
    val imageRes: Int,
    val title: String,
    val subtitle: String
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPageData(
            imageRes = R.drawable.onboarding_page1,
            title = "Sprint Boards",
            subtitle = "Keep track of your tasks and columns collaboratively with your team members in one place."
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding_page2,
            title = "Peer Reviews",
            subtitle = "Gain constructive feedback and peer reviews from team members to accelerate velocity."
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding_page3,
            title = "Hit Your Goals",
            subtitle = "Organize retrospectives, document action items, and achieve your product goals."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = YellowAmbatu,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Horizontal Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val data = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Page Image
                    Image(
                        painter = painterResource(id = data.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .fillMaxHeight(0.45f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(WhiteAmbatu),
                        contentScale = ContentScale.Fit
                    )
                    
                    Spacer(modifier = Modifier.height(36.dp))
                    
                    // Title
                    Text(
                        text = data.title,
                        fontFamily = MontserratFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ChocoAmbatu,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Subtitle
                    Text(
                        text = data.subtitle,
                        fontFamily = MontserratFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkChocoAmbatu.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            // Bottom Navigation Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentPage = pagerState.currentPage
                val hasPrevious = currentPage > 0
                val isLastPage = currentPage == pages.size - 1

                // Left Slot (Previous Button)
                Box(
                    modifier = Modifier.width(96.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (hasPrevious) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(currentPage - 1)
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = ChocoAmbatu)
                        ) {
                            Text(
                                text = "Prev",
                                fontFamily = MontserratFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Middle Slot (Fluid Dots Indicator)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            label = "width"
                        )
                        Box(
                            modifier = Modifier
                                .size(width = width, height = 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) ChocoAmbatu else ChocoAmbatu.copy(alpha = 0.25f)
                                )
                        )
                    }
                }

                // Right Slot (Next/Finish Button)
                Box(
                    modifier = Modifier.width(130.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = {
                            if (isLastPage) {
                                onFinish()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(currentPage + 1)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChocoAmbatu,
                            contentColor = WhiteAmbatu
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (isLastPage) "Get Started" else "Next",
                            fontFamily = MontserratFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
