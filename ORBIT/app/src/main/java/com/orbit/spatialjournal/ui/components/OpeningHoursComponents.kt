package com.orbit.spatialjournal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orbit.spatialjournal.core.model.OpeningHoursEntry
import com.orbit.spatialjournal.core.model.OpeningStatus
import com.orbit.spatialjournal.core.util.DateTimeUtils

/** Shows "Open now · closes in 2h 15m" or "Closed · opens in 3h 40m" for a Place. */
@Composable
fun OpeningStatusBadge(status: OpeningStatus?, modifier: Modifier = Modifier) {
    if (status == null) return
    val label = if (status.isOpenNow) {
        "Open now" + (status.minutesUntilNextChange?.let { " · closes in ${formatMinutes(it)}" } ?: "")
    } else {
        "Closed" + (status.minutesUntilNextChange?.let { " · opens in ${formatMinutes(it)}" } ?: "")
    }
    val color = if (status.isOpenNow) Color(0xFF2E9E5B) else Color(0xFFC1443C)
    AssistChip(
        onClick = {}, label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(labelColor = color)
    )
}

private fun formatMinutes(totalMinutes: Long): String {
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return when {
        h > 0 && m > 0 -> "${h}h ${m}m"
        h > 0 -> "${h}h"
        else -> "${m}m"
    }
}

/** Lets the user enter opening hours for a Place, one row per day of the week. */
@Composable
fun OpeningHoursEditor(
    hours: List<OpeningHoursEntry>,
    onChange: (List<OpeningHoursEntry>) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Column(modifier = modifier) {
        for (isoDay in 1..7) {
            val entry = hours.firstOrNull { it.isoDayOfWeek == isoDay }
                ?: OpeningHoursEntry(isoDayOfWeek = isoDay, closedAllDay = true)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Text(dayLabels[isoDay - 1], modifier = Modifier.width(48.dp))
                Switch(
                    checked = !entry.closedAllDay,
                    onCheckedChange = { open ->
                        val updated = entry.copy(
                            closedAllDay = !open,
                            openMinuteOfDay = if (open) entry.openMinuteOfDay ?: 9 * 60 else null,
                            closeMinuteOfDay = if (open) entry.closeMinuteOfDay ?: 18 * 60 else null
                        )
                        onChange(hours.filterNot { it.isoDayOfWeek == isoDay } + updated)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (!entry.closedAllDay) {
                    Text("${minutesToClock(entry.openMinuteOfDay ?: 0)} – ${minutesToClock(entry.closeMinuteOfDay ?: 0)}")
                } else {
                    Text("Closed", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private fun minutesToClock(minutes: Int): String {
    val h = (minutes / 60).toString().padStart(2, '0')
    val m = (minutes % 60).toString().padStart(2, '0')
    return "$h:$m"
}
