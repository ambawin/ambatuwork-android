package win.ambatu.work.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.unit.sp
import win.ambatu.work.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val MontserratFont = GoogleFont("Montserrat")

val MontserratFamily = FontFamily(
    Font(googleFont = MontserratFont, fontProvider = provider),
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.Medium),
)
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = MontserratFamily),
    displayMedium = TextStyle(fontFamily = MontserratFamily),
    displaySmall = TextStyle(fontFamily = MontserratFamily),
    headlineLarge = TextStyle(fontFamily = MontserratFamily),
    headlineMedium = TextStyle(fontFamily = MontserratFamily),
    headlineSmall = TextStyle(fontFamily = MontserratFamily),
    titleLarge = TextStyle(fontFamily = MontserratFamily),
    titleMedium = TextStyle(fontFamily = MontserratFamily),
    titleSmall = TextStyle(fontFamily = MontserratFamily),
    bodyLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = MontserratFamily),
    bodySmall = TextStyle(fontFamily = MontserratFamily),
    labelLarge = TextStyle(fontFamily = MontserratFamily),
    labelMedium = TextStyle(fontFamily = MontserratFamily),
    labelSmall = TextStyle(fontFamily = MontserratFamily)
)