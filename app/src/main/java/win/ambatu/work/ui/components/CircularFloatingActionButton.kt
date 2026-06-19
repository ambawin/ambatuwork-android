package win.ambatu.work.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import win.ambatu.work.ui.theme.WhiteAmbatu
import win.ambatu.work.ui.theme.ChocoAmbatu

@Composable
fun CircularFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        containerColor = WhiteAmbatu,
        contentColor = ChocoAmbatu,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 1.dp,
            pressedElevation = 2.dp,
            focusedElevation = 1.dp,
            hoveredElevation = 1.dp
        ),
        content = content
    )
}
