package com.orbit.spatialjournal.ui.screens.trips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.core.model.TripStatus
import com.orbit.spatialjournal.core.util.DateTimeUtils
import com.orbit.spatialjournal.ui.navigation.Destination

@Composable
fun TripsScreen(navController: NavHostController, viewModel: TripsViewModel = hiltViewModel()) {
    val trips by viewModel.trips.collectAsState()
    val suggested = trips.filter { it.status == TripStatus.SUGGESTED }
    val others = trips.filter { it.status != TripStatus.SUGGESTED }

    Scaffold(topBar = { TopAppBar(title = { Text("Trips") }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            if (suggested.isNotEmpty()) {
                item { Text("Suggested trips", style = MaterialTheme.typography.titleMedium) }
                items(suggested, key = { it.id }) { trip ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(trip.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${DateTimeUtils.formatShortDate(trip.startDate)} – ${trip.endDate?.let { DateTimeUtils.formatShortDate(it) } ?: ""}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                TextButton(onClick = { viewModel.acceptSuggestion(trip.id) }) { Text("Accept") }
                                TextButton(onClick = { viewModel.rejectSuggestion(trip.id) }) { Text("Dismiss") }
                            }
                        }
                    }
                }
            }

            item { Text("All trips", style = MaterialTheme.typography.titleMedium) }
            items(others, key = { it.id }) { trip ->
                ElevatedCard(
                    onClick = { navController.navigate(Destination.TripDetail.with(trip.id)) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(trip.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${DateTimeUtils.formatShortDate(trip.startDate)} · ${trip.memoryIds.size} memories",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
