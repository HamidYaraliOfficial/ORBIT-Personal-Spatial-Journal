package com.orbit.spatialjournal.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.ui.components.MemoryCard
import com.orbit.spatialjournal.ui.components.OrbitSearchField
import com.orbit.spatialjournal.ui.navigation.Destination

@Composable
fun SearchScreen(navController: NavHostController, viewModel: SearchViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Search") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OrbitSearchField(
                value = state.query, onValueChange = viewModel::setQuery, onSearch = { viewModel.search() },
                placeholder = "Try: \"where was I last summer\""
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isSearching) {
                CircularProgressIndicator()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.results, key = { it.id }) { memory ->
                        MemoryCard(memory = memory, onClick = { navController.navigate(Destination.MemoryDetail.with(memory.id)) })
                    }
                }
            }
        }
    }
}
