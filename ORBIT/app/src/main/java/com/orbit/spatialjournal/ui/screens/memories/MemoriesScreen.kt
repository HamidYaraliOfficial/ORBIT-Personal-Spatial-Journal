package com.orbit.spatialjournal.ui.screens.memories

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.ui.components.MemoryCard
import com.orbit.spatialjournal.ui.navigation.Destination

@Composable
fun MemoriesScreen(navController: NavHostController, viewModel: MemoriesViewModel = hiltViewModel()) {
    val memories by viewModel.memories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memories") },
                actions = {
                    IconButton(onClick = { navController.navigate(Destination.Search.route) }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(memories, key = { it.id }) { memory ->
                MemoryCard(
                    memory = memory,
                    onClick = { navController.navigate(Destination.MemoryDetail.with(memory.id)) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
