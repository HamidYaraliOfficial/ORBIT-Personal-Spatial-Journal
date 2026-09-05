package com.orbit.spatialjournal.ui.screens.timeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.core.model.TimelineGranularity
import com.orbit.spatialjournal.core.util.DateTimeUtils
import com.orbit.spatialjournal.ui.components.MemoryCard
import com.orbit.spatialjournal.ui.navigation.Destination

/**
 * Timeline is kept in sync with the Map through the shared MemoryRepository query window:
 * tapping a Memory here navigates to its detail screen, and Memory Detail's "show on map"
 * action opens Map already centered on that memory's location (see MemoryDetailScreen).
 */
@Composable
fun TimelineScreen(navController: NavHostController, viewModel: TimelineViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Timeline") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = TimelineGranularity.entries.indexOf(state.granularity)) {
                TimelineGranularity.entries.forEach { g ->
                    Tab(
                        selected = state.granularity == g,
                        onClick = { viewModel.setGranularity(g) },
                        text = { Text(g.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.stepBackward() }) { Icon(Icons.Filled.ChevronLeft, null) }
                Text(DateTimeUtils.formatShortDate(state.anchorEpoch), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { viewModel.stepForward() }) { Icon(Icons.Filled.ChevronRight, null) }
            }

            if (state.memories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No memories in this period", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.memories, key = { it.id }) { memory ->
                        MemoryCard(memory = memory, onClick = { navController.navigate(Destination.MemoryDetail.with(memory.id)) })
                    }
                }
            }
        }
    }
}
