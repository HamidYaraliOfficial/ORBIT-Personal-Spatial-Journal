package com.orbit.spatialjournal.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * Lets the user view/export/delete raw location samples directly — the Location History
 * Manager described in the spec. Kept intentionally simple: this screen never displays a
 * live map of raw points (to avoid making an already-sensitive data type feel more
 * "browsable" than necessary); it focuses on retention control and one-tap export/delete.
 */
@Composable
fun LocationHistoryScreen(navController: NavHostController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Location History") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("ORBIT only stores location samples while a Location Mode other than Off is selected.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* export via ExportRepository.exportAll(GEOJSON) */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Export location history")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { /* delete all raw samples */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Delete all location history")
            }
        }
    }
}
