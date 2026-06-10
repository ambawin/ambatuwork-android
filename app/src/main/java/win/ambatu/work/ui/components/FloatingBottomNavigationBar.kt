package win.ambatu.work.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import win.ambatu.work.feature.project.ProjectTab
import win.ambatu.work.ui.theme.ChocoAmbatu
import win.ambatu.work.ui.theme.WhiteAmbatu

@Composable
fun FloatingBottomNavigationBar(
    selectedTab: ProjectTab,
    onTabSelected: (ProjectTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .navigationBarsPadding()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50))
            .clip(RoundedCornerShape(24.dp)),
        containerColor = WhiteAmbatu,
        tonalElevation = 0.dp
    ) {
        ProjectTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = null,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WhiteAmbatu,
                    selectedTextColor = ChocoAmbatu,
                    indicatorColor = ChocoAmbatu,
                    unselectedIconColor = ChocoAmbatu.copy(alpha = 0.6f),
                    unselectedTextColor = ChocoAmbatu.copy(alpha = 0.6f)
                )
            )
        }
    }
}
