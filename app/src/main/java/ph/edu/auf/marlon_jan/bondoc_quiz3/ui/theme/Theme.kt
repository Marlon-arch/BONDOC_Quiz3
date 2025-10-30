package ph.edu.auf.marlon_jan.bondoc_quiz3.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = WhiteBg,
    secondary = BlueLight,
    background = WhiteBg,
    surface = WhiteBg,
    onBackground = BlackText,
    onSurface = BlackText
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
