package com.orbit.spatialjournal.ui.screens.places

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.ui.components.MemoryCard
import com.orbit.spatialjournal.ui.components.OpeningHoursEditor
import com.orbit.spatialjournal.ui.components.OpeningStatusBadge
import com.orbit.spatialjournal.ui.navigation.Destination

@Composable
fun PlaceDetailScreen(navController: NavHostController, placeId: String, viewModel: PlaceDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showHoursEditor by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.place?.name ?: "Place") },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(if (state.place?.isFavorite == true) Icons.Filled.Star else Icons.Filled.StarBorder, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        val place = state.place ?: return@Scaffold
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("${place.city ?: ""}${if (place.country != null) ", ${place.country}" else ""}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            OpeningStatusBadge(state.openingStatus)
            TextButton(onClick = { showHoursEditor = !showHoursEditor }) {
                Text(if (showHoursEditor) "Hide hours editor" else "Set opening hours")
            }
            if (showHoursEditor) {
                OpeningHoursEditor(hours = place.openingHours, onChange = { viewModel.updateOpeningHours(it) })
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("${place.memoryCount} memories")
                Text("${place.photoCount} photos")
                Text("${place.voiceCount} voice")
            }

            if (place.topTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Frequently mentioned: ${place.topTags.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Memories here", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            state.memories.forEach { memory ->
                MemoryCard(
                    memory = memory,
                    onClick = { navController.navigate(Destination.MemoryDetail.with(memory.id)) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
