package com.orbit.spatialjournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.MemoryType
import com.orbit.spatialjournal.core.util.DateTimeUtils

@Composable
fun MemoryCard(memory: Memory, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape)
                    .background(memoryTypeColor(memory.type).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(memoryTypeIcon(memory.type), contentDescription = memory.type.name, tint = memoryTypeColor(memory.type))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(memory.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                val subtitle = buildString {
                    append(DateTimeUtils.formatShortDate(memory.timestamp))
                    memory.placeName?.let { append(" · $it") }
                }
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (memory.isFavorite) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun memoryTypeIcon(type: MemoryType) = when (type) {
    MemoryType.PHOTO -> Icons.Filled.PhotoCamera
    MemoryType.VIDEO -> Icons.Filled.Videocam
    MemoryType.VOICE -> Icons.Filled.Mic
    MemoryType.NOTE -> Icons.Filled.Notes
    MemoryType.JOURNAL -> Icons.Filled.Book
    MemoryType.EVENT -> Icons.Filled.Event
    MemoryType.PLACE -> Icons.Filled.Place
    MemoryType.TRIP -> Icons.Filled.Flight
    MemoryType.TASK -> Icons.Filled.CheckCircle
    MemoryType.BOOKMARK -> Icons.Filled.Bookmark
    MemoryType.DOCUMENT -> Icons.Filled.Description
    MemoryType.CUSTOM -> Icons.Filled.Star
}

@Composable
fun memoryTypeColor(type: MemoryType) = when (type) {
    MemoryType.PHOTO -> com.orbit.spatialjournal.ui.theme.MarkerPhoto
    MemoryType.VIDEO -> com.orbit.spatialjournal.ui.theme.MarkerVideo
    MemoryType.VOICE -> com.orbit.spatialjournal.ui.theme.MarkerVoice
    MemoryType.NOTE -> com.orbit.spatialjournal.ui.theme.MarkerNote
    MemoryType.JOURNAL -> com.orbit.spatialjournal.ui.theme.MarkerJournal
    MemoryType.EVENT -> com.orbit.spatialjournal.ui.theme.MarkerEvent
    MemoryType.PLACE -> com.orbit.spatialjournal.ui.theme.MarkerPlace
    MemoryType.TRIP -> com.orbit.spatialjournal.ui.theme.MarkerTrip
    else -> MaterialTheme.colorScheme.primary
}
