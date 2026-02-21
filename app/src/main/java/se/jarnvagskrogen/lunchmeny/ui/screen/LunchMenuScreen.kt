package se.jarnvagskrogen.lunchmeny.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import se.jarnvagskrogen.lunchmeny.data.model.DayMenu
import se.jarnvagskrogen.lunchmeny.viewmodel.LunchMenuViewModel
import se.jarnvagskrogen.lunchmeny.viewmodel.MenuUiState
import java.time.DayOfWeek

private const val HERO_IMAGE_URL =
    "https://impro.usercontent.one/appid/oneComWsb/domain/jarnvagskrogen.se" +
        "/media/jarnvagskrogen.se/onewebmedia/SMP_5405%20MU.jpg" +
        "?etag=%22a9515-576a74df%22&sourceContentType=image%2Fjpeg&quality=85"

private val DAY_CHIPS = listOf(
    DayOfWeek.MONDAY to "Mån",
    DayOfWeek.TUESDAY to "Tis",
    DayOfWeek.WEDNESDAY to "Ons",
    DayOfWeek.THURSDAY to "Tor",
    DayOfWeek.FRIDAY to "Fre",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchMenuScreen(viewModel: LunchMenuViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            uiState.isLoading -> LoadingContent()
            uiState.errorMessage != null -> ErrorContent(
                message = uiState.errorMessage!!,
                onRetry = { viewModel.retry() },
            )
            else -> PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize(),
            ) {
                MenuContent(
                    uiState = uiState,
                    onDaySelected = { viewModel.selectDay(it) },
                )
            }
        }
    }
}

@Composable
private fun HeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
    ) {
        AsyncImage(
            model = HERO_IMAGE_URL,
            contentDescription = "Järnvägskrogen",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Gradient scrim for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.65f),
                        ),
                    )
                ),
        )

        // Title overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
        ) {
            Text(
                text = "Veckans Lunch",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Järnvägskrogen \u2022 Gävle Centralstation",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun MenuContent(
    uiState: MenuUiState,
    onDaySelected: (DayOfWeek) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // Hero image (draws behind status bar)
        item {
            HeroHeader()
        }

        // Weekend banner
        if (uiState.isWeekend) {
            item {
                WeekendBanner()
            }
        }

        // Day selector chips
        item {
            DaySelector(
                selectedDay = uiState.selectedDayOfWeek,
                availableDays = uiState.weekMenu.map { it.dayOfWeek }.toSet(),
                onDaySelected = onDaySelected,
            )
        }

        // Menu for selected day
        val selectedMenu = uiState.weekMenu.find { it.dayOfWeek == uiState.selectedDayOfWeek }

        if (selectedMenu != null) {
            item {
                DayMenuCard(dayMenu = selectedMenu)
            }
        } else if (uiState.weekMenu.isNotEmpty()) {
            item {
                NoMenuForDay()
            }
        }

        // Info footer
        item {
            InfoFooter()
        }
    }
}

@Composable
private fun WeekendBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Ny meny publiceras normalt på måndag.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun DaySelector(
    selectedDay: DayOfWeek,
    availableDays: Set<DayOfWeek>,
    onDaySelected: (DayOfWeek) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(DAY_CHIPS) { (dayOfWeek, label) ->
            val isAvailable = availableDays.contains(dayOfWeek)
            FilterChip(
                selected = dayOfWeek == selectedDay,
                onClick = { onDaySelected(dayOfWeek) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                enabled = isAvailable,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    }
}

@Composable
private fun DayMenuCard(dayMenu: DayMenu) {
    AnimatedContent(
        targetState = dayMenu,
        transitionSpec = {
            val targetIndex = DAY_CHIPS.indexOfFirst { it.first == targetState.dayOfWeek }
            val initialIndex = DAY_CHIPS.indexOfFirst { it.first == initialState.dayOfWeek }
            val direction = if (targetIndex >= initialIndex) 1 else -1

            (slideInHorizontally { width -> direction * width / 4 } + fadeIn())
                .togetherWith(slideOutHorizontally { width -> -direction * width / 4 } + fadeOut())
        },
        label = "day_menu_animation",
    ) { menu ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Text(
                    text = menu.dayName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(Modifier.height(16.dp))

                menu.dishes.forEachIndexed { index, dish ->
                    DishItem(dish = dish)
                    if (index < menu.dishes.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DishItem(dish: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = dish,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun InfoFooter() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Lunch serveras vardagar 10:30\u201314:30",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Pris: 125 kr  \u2022  Matlåda: 97 kr",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Buffé inkl. sallad, bröd, dryck, kaffe & kaka",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NoMenuForDay() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Ingen meny tillgänglig för denna dag",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Hämtar veckans meny\u2026",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "Kunde inte ladda menyn",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = onRetry) {
                Icon(Icons.Rounded.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Försök igen")
            }
        }
    }
}
