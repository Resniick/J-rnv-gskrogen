package se.jarnvagskrogen.lunchmeny.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import se.jarnvagskrogen.lunchmeny.data.model.DayMenu
import java.time.DayOfWeek

class MenuRepository {

    companion object {
        private const val MENU_URL = "https://jarnvagskrogen.se/lunchmeny.html"
        private const val TIMEOUT_MS = 15_000

        private val DAY_ENTRIES = listOf(
            "Måndag" to DayOfWeek.MONDAY,
            "Tisdag" to DayOfWeek.TUESDAY,
            "Onsdag" to DayOfWeek.WEDNESDAY,
            "Torsdag" to DayOfWeek.THURSDAY,
            "Fredag" to DayOfWeek.FRIDAY,
        )
    }

    suspend fun fetchWeekMenu(): Result<List<DayMenu>> = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect(MENU_URL)
                .timeout(TIMEOUT_MS)
                .get()

            val menus = parseMenu(doc.select("p"))
            if (menus.isEmpty()) {
                Result.failure(Exception("Kunde inte hitta menyn på sidan"))
            } else {
                Result.success(menus)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseMenu(paragraphs: org.jsoup.select.Elements): List<DayMenu> {
        val menus = mutableListOf<DayMenu>()
        var currentDay: Pair<String, DayOfWeek>? = null
        var currentDishes = mutableListOf<String>()

        for (p in paragraphs) {
            val text = p.text().trim()

            val matchedDay = DAY_ENTRIES.firstOrNull { (name, _) ->
                text.startsWith(name, ignoreCase = true)
            }

            if (matchedDay != null) {
                // Save previous day's menu
                if (currentDay != null && currentDishes.isNotEmpty()) {
                    menus.add(DayMenu(currentDay.first, currentDay.second, currentDishes.toList()))
                }
                currentDay = matchedDay
                currentDishes = mutableListOf()
            } else if (currentDay != null && text.isNotBlank() && text != "\u00a0") {
                val lowerText = text.lowercase()
                // "Smaklig Måltid" marks the end of the menu
                if (lowerText.contains("smaklig måltid")) {
                    break
                }
                currentDishes.add(text)
            }
        }

        // Don't forget the last day
        if (currentDay != null && currentDishes.isNotEmpty()) {
            menus.add(DayMenu(currentDay.first, currentDay.second, currentDishes.toList()))
        }

        return menus
    }
}
