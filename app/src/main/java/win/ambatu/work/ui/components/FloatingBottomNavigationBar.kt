package win.ambatu.work.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import win.ambatu.work.feature.project.ProjectTab
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.HazeTint

@Composable
fun FloatingBottomNavigationBar( 
    selectedTab: ProjectTab,
    onTabSelected: (ProjectTab) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val barModifier = if (hazeState != null) {
        modifier
            .padding(horizontal = 24.dp, vertical = 2.dp)
            .navigationBarsPadding()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(50))
            .clip(RoundedCornerShape(24.dp))
            .hazeEffect(state = hazeState) {
                blurRadius = 15.dp
                tints = listOf(HazeTint(color = WhiteAmbatu.copy(alpha = 0.50f)))
            }
    } else {
        modifier
            .padding(horizontal = 24.dp, vertical = 2.dp)
            .navigationBarsPadding()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(50))
            .clip(RoundedCornerShape(24.dp))
    }

    NavigationBar(
        modifier = barModifier,
        containerColor = if (hazeState != null) Color.Transparent else WhiteAmbatu,
        tonalElevation = 0.dp
    ) {
        ProjectTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = false,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.semantics { this.selected = isSelected },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ChocoAmbatu else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) WhiteAmbatu else ChocoAmbatu.copy(alpha = 0.6f)
                        )
                    }
                },
                label = null,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = WhiteAmbatu,
                    selectedTextColor = ChocoAmbatu,
                    unselectedIconColor = ChocoAmbatu.copy(alpha = 0.6f),
                    unselectedTextColor = ChocoAmbatu.copy(alpha = 0.6f)
                )
            )
        }
    }
}

