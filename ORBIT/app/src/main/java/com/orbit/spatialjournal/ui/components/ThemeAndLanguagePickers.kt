package com.orbit.spatialjournal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbit.spatialjournal.core.model.AccentStyle
import com.orbit.spatialjournal.core.model.AppLanguage
import com.orbit.spatialjournal.core.model.ThemeMode

@Composable
fun ThemeModePicker(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    Column {
        Text("Appearance", style = MaterialTheme.typography.titleMedium)
        ThemeMode.values().forEach { mode ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().selectable(selected == mode) { onSelect(mode) }.padding(vertical = 6.dp)
            ) {
                RadioButton(selected = selected == mode, onClick = { onSelect(mode) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }
    }
}

@Composable
fun AccentStylePicker(selected: AccentStyle, onSelect: (AccentStyle) -> Unit) {
    Column {
        Text("Accent", style = MaterialTheme.typography.titleMedium)
        AccentStyle.values().forEach { style ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().selectable(selected == style) { onSelect(style) }.padding(vertical = 6.dp)
            ) {
                RadioButton(selected = selected == style, onClick = { onSelect(style) })
                Spacer(modifier = Modifier.width(8.dp))
                val label = when (style) {
                    AccentStyle.WINDOWS11 -> "Windows 11 (default)"
                    AccentStyle.RED -> "Red"
                    AccentStyle.BLUE -> "Blue"
                }
                Text(label)
            }
        }
    }
}

@Composable
fun LanguagePicker(selected: AppLanguage, onSelect: (AppLanguage) -> Unit) {
    Column {
        Text("Language / زبان / 语言", style = MaterialTheme.typography.titleMedium)
        AppLanguage.values().forEach { lang ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().selectable(selected == lang) { onSelect(lang) }.padding(vertical = 6.dp)
            ) {
                RadioButton(selected = selected == lang, onClick = { onSelect(lang) })
                Spacer(modifier = Modifier.width(8.dp))
                val label = when (lang) {
                    AppLanguage.ENGLISH -> "English"
                    AppLanguage.PERSIAN -> "فارسی"
                    AppLanguage.CHINESE -> "中文"
                }
                Text(label)
            }
        }
    }
}
