package com.orbit.spatialjournal.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.ui.components.MemoryCard
import com.orbit.spatialjournal.ui.components.StatCard
import com.orbit.spatialjournal.ui.navigation.Destination

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val recap by viewModel.recap.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("ORBIT") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Destination.Capture.withType(null)) },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Capture") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Memories", state.totalMemoryCount.toString(), modifier = Modifier.weight(1f))
                    StatCard("Suggested trips", state.suggestedTrips.size.toString(), modifier = Modifier.weight(1f))
                }
            }

            recap?.let { r ->
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Today's recap", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (r.memoryCount == 0) "No memories captured today yet." else r.narrative,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Recent memories", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { navController.navigate(Destination.Memories.route) }) { Text("See all") }
                }
            }
            items(state.recentMemories, key = { it.id }) { memory ->
                MemoryCard(memory = memory, onClick = { navController.navigate(Destination.MemoryDetail.with(memory.id)) })
            }

            if (state.suggestedTrips.isNotEmpty()) {
                item { Text("Suggested trips", style = MaterialTheme.typography.titleMedium) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.suggestedTrips, key = { it.id }) { trip ->
                            ElevatedCard(onClick = { navController.navigate(Destination.TripDetail.with(trip.id)) }) {
                                Column(modifier = Modifier.padding(12.dp).width(160.dp)) {
                                    Text(trip.name, maxLines = 2)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { navController.navigate(Destination.Search.route) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Search, contentDescription = null); Spacer(Modifier.width(6.dp)); Text("Search")
                    }
                    OutlinedButton(onClick = { navController.navigate(Destination.Graph.route) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Hub, contentDescription = null); Spacer(Modifier.width(6.dp)); Text("Graph")
                    }
                }
            }
        }
    }
}
