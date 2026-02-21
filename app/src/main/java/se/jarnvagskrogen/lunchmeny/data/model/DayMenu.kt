package se.jarnvagskrogen.lunchmeny.data.model

import java.time.DayOfWeek

data class DayMenu(
    val dayName: String,
    val dayOfWeek: DayOfWeek,
    val dishes: List<String>
)
