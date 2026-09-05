package com.orbit.spatialjournal.ui.screens.memories

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.orbit.spatialjournal.core.model.MemoryType
import com.orbit.spatialjournal.ui.components.TagChip
import com.orbit.spatialjournal.ui.navigation.Destination

/**
 * Smart Memory Capture: pick a type (or it's pre-selected from a Quick Capture shortcut/widget),
 * optionally attach a photo/voice/location, then save. Every field remains editable before save.
 */
@Composable
fun CaptureScreen(navController: NavHostController, initialType: String, viewModel: CaptureViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var tagInput by remember { mutableStateOf("") }
    var pickedUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(initialType) {
        MemoryType.entries.firstOrNull { it.name.equals(initialType, ignoreCase = true) }?.let { viewModel.setType(it) }
        viewModel.captureCurrentContext()
    }

    LaunchedEffect(state.savedMemoryId) {
        state.savedMemoryId?.let {
            navController.popBackStack()
            navController.navigate(Destination.MemoryDetail.with(it))
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { pickedUri = it.toString() }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Capture") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listOf(MemoryType.PHOTO, MemoryType.VIDEO, MemoryType.VOICE, MemoryType.NOTE, MemoryType.JOURNAL, MemoryType.EVENT, MemoryType.TASK, MemoryType.BOOKMARK)) { type ->
                    FilterChip(selected = state.type == type, onClick = { viewModel.setType(type) }, label = { Text(type.name) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = state.title, onValueChange = viewModel::setTitle, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.description, onValueChange = viewModel::setDescription,
                label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                OutlinedTextField(
                    value = tagInput, onValueChange = { tagInput = it },
                    label = { Text("Add tag") }, modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { viewModel.addTag(tagInput); tagInput = "" }) { Text("Add") }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(state.tags) { tag -> TagChip(tag) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (state.type == MemoryType.PHOTO || state.type == MemoryType.VIDEO) {
                OutlinedButton(onClick = { photoPicker.launch("image/*") }) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null); Spacer(Modifier.width(6.dp))
                    Text(if (pickedUri != null) "Photo selected" else "Choose photo")
                }
            }

            if (state.type == MemoryType.VOICE) {
                OutlinedButton(onClick = {
                    if (state.isRecordingVoice) {
                        val path = viewModel.stopVoiceRecording()
                        pickedUri = path
                    } else {
                        viewModel.startVoiceRecording()
                    }
                }) {
                    Icon(if (state.isRecordingVoice) Icons.Filled.Stop else Icons.Filled.Mic, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (state.isRecordingVoice) "Stop recording" else "Record voice note")
                }
            }

            state.location?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("📍 Location captured (${"%.4f".format(it.latitude)}, ${"%.4f".format(it.longitude)})", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { viewModel.save(pickedUri, mimeType = null) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp)) else Text("Save memory")
            }
        }
    }
}
