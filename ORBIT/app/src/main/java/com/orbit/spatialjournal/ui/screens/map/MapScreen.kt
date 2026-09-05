package com.orbit.spatialjournal.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.orbit.spatialjournal.core.util.DateTimeUtils
import com.orbit.spatialjournal.ui.components.memoryTypeColor
import com.orbit.spatialjournal.ui.navigation.Destination
import kotlinx.coroutines.launch

/**
 * Interactive Map Screen. Uses Google Maps Compose with app-level clustering
 * (MarkerClusterManager) so marker density adapts to zoom, plus a Region Summary card
 * that appears automatically when zoomed out to a city/country level.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavHostController, viewModel: MapViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 2f)
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(cameraPositionState.position.zoom) {
        viewModel.onZoomChanged(cameraPositionState.position.zoom)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val loc = viewModel.locationManager.getCurrentLocationOnce()
                            loc?.let {
                                cameraPositionState.animate(
                                    com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                                        LatLng(it.point.latitude, it.point.longitude), 14f
                                    )
                                )
                            }
                        }
                    }) { Icon(Icons.Filled.MyLocation, contentDescription = "Locate me") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                state.clusters.forEach { cluster ->
                    val position = LatLng(cluster.center.latitude, cluster.center.longitude)
                    if (cluster.size == 1) {
                        val memory = cluster.memories.first()
                        Marker(
                            state = rememberMarkerState(position = position),
                            title = memory.title,
                            snippet = DateTimeUtils.formatShortDate(memory.timestamp),
                            onClick = { viewModel.onClusterClick(cluster); true }
                        )
                    } else {
                        Marker(
                            state = rememberMarkerState(position = position),
                            title = "${cluster.size} memories",
                            onClick = { viewModel.onClusterClick(cluster); true }
                        )
                    }
                }
            }

            // Type filter row
            Row(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                com.orbit.spatialjournal.core.model.MemoryType.entries.take(5).forEach { type ->
                    FilterChip(
                        selected = type in state.typeFilter,
                        onClick = { viewModel.toggleTypeFilter(type) },
                        label = { Text(type.name.lowercase()) }
                    )
                }
            }

            state.regionSummary?.let { summary ->
                if (summary.memoryCount > 0) {
                    ElevatedCard(modifier = Modifier.align(Alignment.TopCenter).padding(top = 56.dp).padding(horizontal = 16.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${summary.memoryCount} memories in view", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${summary.photoCount} photos · ${summary.voiceCount} voice notes",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (summary.topTags.isNotEmpty()) {
                                Text("Top: ${summary.topTags.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            state.selectedCluster?.let { cluster ->
                ModalBottomSheet(onDismissRequest = { viewModel.dismissSelection() }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (cluster.size == 1) {
                            val memory = cluster.memories.first()
                            Text(memory.title, style = MaterialTheme.typography.titleLarge)
                            Text(DateTimeUtils.formatShortDate(memory.timestamp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = {
                                viewModel.dismissSelection()
                                navController.navigate(Destination.MemoryDetail.with(memory.id))
                            }) { Text("Open detail") }
                        } else {
                            Text("${cluster.size} memories here", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            cluster.memories.take(6).forEach { memory ->
                                ListItem(
                                    headlineContent = { Text(memory.title) },
                                    supportingContent = { Text(DateTimeUtils.formatShortDate(memory.timestamp)) },
                                    modifier = Modifier.clickableNavigate {
                                        viewModel.dismissSelection()
                                        navController.navigate(Destination.MemoryDetail.with(memory.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Modifier.clickableNavigate(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))
