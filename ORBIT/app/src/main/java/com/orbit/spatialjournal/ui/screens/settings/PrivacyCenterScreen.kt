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
import com.orbit.spatialjournal.ui.navigation.Destination

/**
 * The Privacy Center: every data-access toggle described in the spec lives here, with a
 * plain-language explanation next to each one, so nothing about what ORBIT can see is hidden
 * behind a generic "Permissions" system screen.
 */
@Composable
fun PrivacyCenterScreen(navController: NavHostController, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Privacy Center") }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            item { Text("Data access", style = MaterialTheme.typography.titleMedium) }
            item {
                SwitchRow(
                    title = "AI access to your memories",
                    subtitle = "Lets the on-device assistant read your memories to build recaps and stories. Nothing leaves your device.",
                    checked = state.aiAccessEnabled, onCheckedChange = viewModel::setAiAccessEnabled
                )
            }
            item {
                SwitchRow(
                    title = "Cloud AI (optional)",
                    subtitle = "Off by default. If enabled, some AI requests may be sent to a cloud provider instead of running on-device.",
                    checked = state.cloudAiEnabled, onCheckedChange = viewModel::setCloudAiEnabled
                )
            }
            item {
                SwitchRow(
                    title = "Anonymous usage analytics",
                    subtitle = "Off by default. Never includes memory content or precise location.",
                    checked = state.analyticsEnabled, onCheckedChange = viewModel::setAnalyticsEnabled
                )
            }
            item {
                SwitchRow(
                    title = "App Lock (biometric)",
                    subtitle = "Require fingerprint/face or device credential to open ORBIT.",
                    checked = state.appLockEnabled, onCheckedChange = viewModel::setAppLockEnabled
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)); Text("Location data", style = MaterialTheme.typography.titleMedium) }
            item {
                ListItem(
                    headlineContent = { Text("Location History") },
                    supportingContent = { Text("View, export or delete raw location samples") },
                    modifier = Modifier.thenClickable { navController.navigate(Destination.LocationHistory.route) }
                )
            }
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("Auto-delete location history after ${state.locationHistoryRetentionDays} days")
                    Slider(
                        value = state.locationHistoryRetentionDays.toFloat(),
                        onValueChange = { viewModel.setRetentionDays(it.toInt()) },
                        valueRange = 7f..365f
                    )
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun Modifier.thenClickable(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))
