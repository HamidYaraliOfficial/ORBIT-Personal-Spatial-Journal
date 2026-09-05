package com.orbit.spatialjournal.ui.screens.trips

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.ui.components.MemoryCard
import com.orbit.spatialjournal.ui.navigation.Destination

@Composable
fun TripDetailScreen(navController: NavHostController, tripId: String, viewModel: TripDetailViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(state.trip?.name ?: "Trip") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row {
                Text("${state.memories.size} memories", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.generateAiSummary() }) { Text("Generate AI trip summary") }

            state.story?.let { story ->
                Spacer(modifier = Modifier.height(12.dp))
                ElevatedCard {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(story.title, style = MaterialTheme.typography.titleMedium)
                        Text(story.introduction, style = MaterialTheme.typography.bodyMedium)
                        story.sections.forEach { section ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(section.heading, style = MaterialTheme.typography.titleSmall)
                            Text(section.body, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Memories", style = MaterialTheme.typography.titleMedium)
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
