package se.jarnvagskrogen.lunchmeny

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import se.jarnvagskrogen.lunchmeny.ui.screen.LunchMenuScreen
import se.jarnvagskrogen.lunchmeny.ui.theme.JarnvagskrogenTheme
import se.jarnvagskrogen.lunchmeny.viewmodel.LunchMenuViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LunchMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        enableEdgeToEdge(
            statusBarStyle = if (isDarkMode) {
                SystemBarStyle.dark(android.graphics.Color.rgb(0x11, 0x13, 0x18))
            } else {
                SystemBarStyle.light(
                    scrim = android.graphics.Color.rgb(0xF8, 0xFB, 0xFF),
                    darkScrim = android.graphics.Color.rgb(0x11, 0x13, 0x18),
                )
            }
        )
        setContent {
            JarnvagskrogenTheme {
                LunchMenuScreen(viewModel = viewModel)
            }
        }
    }
}
