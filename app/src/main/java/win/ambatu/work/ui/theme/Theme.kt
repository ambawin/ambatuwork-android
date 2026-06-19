package win.ambatu.work.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = YellowAmbatu,
    onPrimary = DarkChocoAmbatu,
    primaryContainer = ChocoAmbatu,
    onPrimaryContainer = YellowAmbatu,
    secondary = MediumYellowAmbatu,
    onSecondary = DarkChocoAmbatu,
    secondaryContainer = DarkChocoAmbatu,
    onSecondaryContainer = MediumYellowAmbatu,
    tertiary = BlueAmbatu,
    onTertiary = WhiteAmbatu,
    tertiaryContainer = Color(0xFF0D356A),
    onTertiaryContainer = LightBlueAmbatu,
    background = DarkChocoAmbatu,
    onBackground = WhiteAmbatu,
    surface = Color(0xFF2C2109),
    onSurface = WhiteAmbatu,
    surfaceVariant = Color(0xFF382B0D),
    onSurfaceVariant = WhiteAmbatu,
    error = RedAmbatu,
    onError = WhiteAmbatu,
    errorContainer = Color(0xFF5A1A13),
    onErrorContainer = LightRedAmbatu
)

private val LightColorScheme = lightColorScheme(
    primary = ChocoAmbatu,
    onPrimary = WhiteAmbatu,
    primaryContainer = MediumYellowAmbatu,
    onPrimaryContainer = DarkChocoAmbatu,
    secondary = LightChocoAmbatu,
    onSecondary = WhiteAmbatu,
    secondaryContainer = LightYellowAmbatu,
    onSecondaryContainer = DarkChocoAmbatu,
    tertiary = BlueAmbatu,
    onTertiary = WhiteAmbatu,
    tertiaryContainer = LightBlueAmbatu,
    onTertiaryContainer = BlueAmbatu,
    background = YellowAmbatu,
    onBackground = DarkChocoAmbatu,
    surface = WhiteAmbatu,
    onSurface = DarkChocoAmbatu,
    surfaceVariant = LightYellowAmbatu,
    onSurfaceVariant = ChocoAmbatu,
    outline = ChocoAmbatu,
    outlineVariant = ChocoAmbatu.copy(alpha = 0.2f),
    error = RedAmbatu,
    onError = WhiteAmbatu,
    errorContainer = LightRedAmbatu,
    onErrorContainer = RedAmbatu
)

@Composable
fun AmbatuWorkTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}