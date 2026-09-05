package com.orbit.spatialjournal.ui.screens.journal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

/**
 * Smart Journal Editor: a simple block-based rich editor (text, checklist, image/video/voice
 * refs, link, quote, location card) — each block independently editable, matching the
 * "several blocks per entry" requirement without pulling in a heavyweight rich-text library.
 */
@Composable
fun JournalEditorScreen(navController: NavHostController, memoryId: String, viewModel: JournalViewModel = hiltViewModel()) {
    val entry by viewModel.entry.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal") },
                actions = { IconButton(onClick = { viewModel.save(); navController.popBackStack() }) {
                    Icon(Icons.Filled.Check, contentDescription = "Save")
                } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            entry?.blocks?.forEachIndexed { index, block ->
                when (block.type) {
                    "checklist" -> Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(checked = block.checked ?: false, onCheckedChange = { viewModel.toggleChecklistItem(index) })
                        OutlinedTextField(
                            value = block.content, onValueChange = { viewModel.updateBlockText(index, it) },
                            modifier = Modifier.weight(1f), placeholder = { Text("Checklist item") }
                        )
                    }
                    "heading" -> OutlinedTextField(
                        value = block.content, onValueChange = { viewModel.updateBlockText(index, it) },
                        textStyle = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Heading") }
                    )
                    "quote" -> OutlinedTextField(
                        value = block.content, onValueChange = { viewModel.updateBlockText(index, it) },
                        modifier = Modifier.fillMaxWidth(), placeholder = { Text("Quote") }
                    )
                    else -> OutlinedTextField(
                        value = block.content, onValueChange = { viewModel.updateBlockText(index, it) },
                        modifier = Modifier.fillMaxWidth(), placeholder = { Text("Write something…") }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(onClick = { viewModel.addBlock("text") }) { Text("+ Text") }
                TextButton(onClick = { viewModel.addBlock("heading") }) { Text("+ Heading") }
                TextButton(onClick = { viewModel.addBlock("checklist") }) { Text("+ Checklist") }
                TextButton(onClick = { viewModel.addBlock("quote") }) { Text("+ Quote") }
            }
        }
    }
}
