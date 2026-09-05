package com.orbit.spatialjournal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class QuickAction(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val onClick: () -> Unit)

@Composable
fun QuickActionSheetContent(actions: List<QuickAction>) {
    Column(modifier = Modifier.padding(16.dp)) {
        actions.forEach { action ->
            ListItem(
                headlineContent = { Text(action.label) },
                leadingContent = { Icon(action.icon, contentDescription = action.label) },
                modifier = Modifier.clickableCompat(action.onClick)
            )
        }
    }
}

@Composable
private fun Modifier.clickableCompat(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))

fun defaultQuickActions(
    onOpenDetail: () -> Unit, onAddTag: () -> Unit, onFavorite: () -> Unit,
    onShare: () -> Unit, onCreateTask: () -> Unit, onAddReminder: () -> Unit, onRelated: () -> Unit
) = listOf(
    QuickAction("Open detail", Icons.Filled.OpenInNew, onOpenDetail),
    QuickAction("Add tag", Icons.Filled.Label, onAddTag),
    QuickAction("Favorite", Icons.Filled.Star, onFavorite),
    QuickAction("Share", Icons.Filled.Share, onShare),
    QuickAction("Create task", Icons.Filled.CheckCircle, onCreateTask),
    QuickAction("Add reminder", Icons.Filled.Alarm, onAddReminder),
    QuickAction("Show related memories", Icons.Filled.Hub, onRelated)
)
