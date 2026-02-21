package se.jarnvagskrogen.lunchmeny.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    secondary = BlueGray40,
    onSecondary = Color.White,
    secondaryContainer = BlueGray90,
    onSecondaryContainer = BlueGray10,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Color.White,
    onSurface = Neutral10,
    surfaceVariant = NeutralVar90,
    onSurfaceVariant = NeutralVar30,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    secondary = BlueGray80,
    onSecondary = BlueGray20,
    secondaryContainer = BlueGray30,
    onSecondaryContainer = BlueGray90,
    background = Neutral12,
    onBackground = Neutral80,
    surface = Neutral17,
    onSurface = Neutral80,
    surfaceVariant = Neutral22,
    onSurfaceVariant = NeutralVar80,
)

@Composable
fun JarnvagskrogenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
