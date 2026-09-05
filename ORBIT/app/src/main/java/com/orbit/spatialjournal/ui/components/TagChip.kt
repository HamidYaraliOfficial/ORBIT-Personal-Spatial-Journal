package com.orbit.spatialjournal.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TagChip(text: String, onClick: () -> Unit = {}) {
    AssistChip(onClick = onClick, label = { Text("#$text") })
}
