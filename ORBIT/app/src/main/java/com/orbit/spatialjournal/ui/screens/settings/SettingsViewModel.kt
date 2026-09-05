package com.orbit.spatialjournal.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.*
import com.orbit.spatialjournal.data.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentStyle: AccentStyle = AccentStyle.WINDOWS11,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val locationMode: LocationMode = LocationMode.OFF,
    val mapStyle: MapStyleOption = MapStyleOption.STANDARD,
    val aiAccessEnabled: Boolean = true,
    val cloudAiEnabled: Boolean = false,
    val analyticsEnabled: Boolean = false,
    val appLockEnabled: Boolean = false,
    val locationHistoryRetentionDays: Int = 90
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsDataStore.themeMode, settingsDataStore.accentStyle, settingsDataStore.language,
        settingsDataStore.locationMode, settingsDataStore.mapStyle
    ) { theme, accent, lang, locMode, mapStyle ->
        SettingsUiState(themeMode = theme, accentStyle = accent, language = lang, locationMode = locMode, mapStyle = mapStyle)
    }.combine(settingsDataStore.aiAccessEnabled) { s, ai -> s.copy(aiAccessEnabled = ai) }
        .combine(settingsDataStore.cloudAiEnabled) { s, cloud -> s.copy(cloudAiEnabled = cloud) }
        .combine(settingsDataStore.analyticsEnabled) { s, analytics -> s.copy(analyticsEnabled = analytics) }
        .combine(settingsDataStore.appLockEnabled) { s, lock -> s.copy(appLockEnabled = lock) }
        .combine(settingsDataStore.locationHistoryRetentionDays) { s, days -> s.copy(locationHistoryRetentionDays = days) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { settingsDataStore.setThemeMode(mode) }
    fun setAccentStyle(style: AccentStyle) = viewModelScope.launch { settingsDataStore.setAccentStyle(style) }
    fun setLanguage(language: AppLanguage) = viewModelScope.launch { settingsDataStore.setLanguage(language) }
    fun setLocationMode(mode: LocationMode) = viewModelScope.launch { settingsDataStore.setLocationMode(mode) }
    fun setMapStyle(style: MapStyleOption) = viewModelScope.launch { settingsDataStore.setMapStyle(style) }
    fun setAiAccessEnabled(enabled: Boolean) = viewModelScope.launch { settingsDataStore.setAiAccessEnabled(enabled) }
    fun setCloudAiEnabled(enabled: Boolean) = viewModelScope.launch { settingsDataStore.setCloudAiEnabled(enabled) }
    fun setAnalyticsEnabled(enabled: Boolean) = viewModelScope.launch { settingsDataStore.setAnalyticsEnabled(enabled) }
    fun setAppLockEnabled(enabled: Boolean) = viewModelScope.launch { settingsDataStore.setAppLockEnabled(enabled) }
    fun setRetentionDays(days: Int) = viewModelScope.launch { settingsDataStore.setLocationHistoryRetentionDays(days) }
}
