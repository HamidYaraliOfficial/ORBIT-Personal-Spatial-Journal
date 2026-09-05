package com.orbit.spatialjournal.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.item
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.core.model.LocationMode
import com.orbit.spatialjournal.ui.components.AccentStylePicker
import com.orbit.spatialjournal.ui.components.LanguagePicker
import com.orbit.spatialjournal.ui.components.ThemeModePicker
import com.orbit.spatialjournal.ui.navigation.Destination

@Composable
fun SettingsScreen(navController: NavHostController, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { ThemeModePicker(selected = state.themeMode, onSelect = viewModel::setThemeMode) }
            item { AccentStylePicker(selected = state.accentStyle, onSelect = viewModel::setAccentStyle) }
            item { LanguagePicker(selected = state.language, onSelect = viewModel::setLanguage) }

            item {
                Column {
                    Text("Location", style = MaterialTheme.typography.titleMedium)
                    LocationMode.entries.forEach { mode ->
                        ListItem(
                            headlineContent = { Text(mode.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }) },
                            leadingContent = {
                                RadioButton(selected = state.locationMode == mode, onClick = { viewModel.setLocationMode(mode) })
                            }
                        )
                    }
                }
            }

            item {
                ListItem(
                    headlineContent = { Text("Privacy Center") },
                    supportingContent = { Text("Permissions, retention, AI access, app lock") },
                    modifier = Modifier.clickable { navController.navigate(Destination.PrivacyCenter.route) }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Backup & Restore") },
                    modifier = Modifier.clickable { navController.navigate(Destination.Backup.route) }
                )
            }
        }
    }
}

@Composable
private fun Modifier.clickable(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))
