package com.orbit.spatialjournal.ui.screens.memories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.core.util.DateTimeUtils
import com.orbit.spatialjournal.ui.components.MemoryCard
import com.orbit.spatialjournal.ui.components.TagChip
import com.orbit.spatialjournal.ui.navigation.Destination

@Composable
fun MemoryDetailScreen(navController: NavHostController, memoryId: String, viewModel: MemoryDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val memory = state.memory

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(memory?.title ?: "") },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (memory?.isFavorite == true) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Favorite"
                        )
                    }
                    IconButton(onClick = { viewModel.archive(); navController.popBackStack() }) {
                        Icon(Icons.Filled.Archive, contentDescription = "Archive")
                    }
                }
            )
        }
    ) { padding ->
        if (memory == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(DateTimeUtils.formatShortDate(memory.timestamp) + " · " + DateTimeUtils.formatTime(memory.timestamp))
            memory.placeName?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            Spacer(modifier = Modifier.height(12.dp))
            memory.description?.let { Text(it) }

            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(memory.tags) { tag -> TagChip(tag) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (memory.location != null) {
                    OutlinedButton(onClick = { navController.navigate(Destination.Map.route) }) {
                        Icon(Icons.Filled.Map, contentDescription = null); Spacer(Modifier.width(6.dp)); Text("Show on map")
                    }
                }
                OutlinedButton(onClick = { navController.navigate(Destination.JournalEditor.with(memory.id)) }) {
                    Icon(Icons.Filled.Book, contentDescription = null); Spacer(Modifier.width(6.dp)); Text("Journal")
                }
            }

            if (state.relatedMemories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Related memories", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                state.relatedMemories.forEach { related ->
                    MemoryCard(
                        memory = related,
                        onClick = { navController.navigate(Destination.MemoryDetail.with(related.id)) },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}
