package com.orbit.spatialjournal.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.domain.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupScreenViewModel @Inject constructor(
    private val backupRepository: BackupRepository
) : ViewModel() {
    var lastResult by mutableStateOf<String?>(null); private set

    fun createBackup(destinationUri: String, encrypt: Boolean) {
        viewModelScope.launch {
            val manifest = backupRepository.createBackup(destinationUri, encrypt, passphrase = null)
            lastResult = "Backed up ${manifest.memoryCount} memories (${if (manifest.encrypted) "encrypted" else "unencrypted"})"
        }
    }
}

@Composable
fun BackupScreen(navController: NavHostController, viewModel: BackupScreenViewModel = hiltViewModel()) {
    var encrypt by remember { mutableStateOf(true) }

    Scaffold(topBar = { TopAppBar(title = { Text("Backup & Restore") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Switch(checked = encrypt, onCheckedChange = { encrypt = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Encrypt backup (AES-256, Android Keystore)")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* launch SAF create-document, then viewModel.createBackup(uri, encrypt) */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Create backup now")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { /* launch SAF open-document, then restore */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Restore from backup")
            }
            viewModel.lastResult?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
