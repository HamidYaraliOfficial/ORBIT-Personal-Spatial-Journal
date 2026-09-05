package com.orbit.spatialjournal.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.orbit.spatialjournal.core.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "orbit_settings")

/**
 * Single source of truth for every user-facing preference: theme/accent, language, the
 * Location Mode (Off / Manual / While Using / Smart Context / Background Reminders), and the
 * privacy toggles shown in the Privacy Center. Nothing here requires the user to have granted
 * any permission — defaults are always the most private option.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_STYLE = stringPreferencesKey("accent_style")
        val LANGUAGE = stringPreferencesKey("language")
        val LOCATION_MODE = stringPreferencesKey("location_mode")
        val MAP_STYLE = stringPreferencesKey("map_style")
        val AI_ACCESS_ENABLED = booleanPreferencesKey("ai_access_enabled")
        val CLOUD_AI_ENABLED = booleanPreferencesKey("cloud_ai_enabled")
        val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val LOCATION_HISTORY_RETENTION_DAYS = intPreferencesKey("location_history_retention_days")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map {
        it[Keys.THEME_MODE]?.let { v -> runCatching { ThemeMode.valueOf(v) }.getOrNull() } ?: ThemeMode.SYSTEM
    }
    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    val accentStyle: Flow<AccentStyle> = context.dataStore.data.map {
        it[Keys.ACCENT_STYLE]?.let { v -> runCatching { AccentStyle.valueOf(v) }.getOrNull() } ?: AccentStyle.WINDOWS11
    }
    suspend fun setAccentStyle(style: AccentStyle) {
        context.dataStore.edit { it[Keys.ACCENT_STYLE] = style.name }
    }

    val language: Flow<AppLanguage> = context.dataStore.data.map {
        it[Keys.LANGUAGE]?.let { v -> runCatching { AppLanguage.valueOf(v) }.getOrNull() } ?: AppLanguage.ENGLISH
    }
    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.name }
    }

    val locationMode: Flow<LocationMode> = context.dataStore.data.map {
        it[Keys.LOCATION_MODE]?.let { v -> runCatching { LocationMode.valueOf(v) }.getOrNull() } ?: LocationMode.OFF
    }
    suspend fun setLocationMode(mode: LocationMode) {
        context.dataStore.edit { it[Keys.LOCATION_MODE] = mode.name }
    }

    val mapStyle: Flow<MapStyleOption> = context.dataStore.data.map {
        it[Keys.MAP_STYLE]?.let { v -> runCatching { MapStyleOption.valueOf(v) }.getOrNull() } ?: MapStyleOption.STANDARD
    }
    suspend fun setMapStyle(style: MapStyleOption) {
        context.dataStore.edit { it[Keys.MAP_STYLE] = style.name }
    }

    val aiAccessEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.AI_ACCESS_ENABLED] ?: true }
    suspend fun setAiAccessEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AI_ACCESS_ENABLED] = enabled }
    }

    val cloudAiEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.CLOUD_AI_ENABLED] ?: false }
    suspend fun setCloudAiEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.CLOUD_AI_ENABLED] = enabled }
    }

    val analyticsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.ANALYTICS_ENABLED] ?: false }
    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ANALYTICS_ENABLED] = enabled }
    }

    val appLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.APP_LOCK_ENABLED] ?: false }
    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.APP_LOCK_ENABLED] = enabled }
    }

    val locationHistoryRetentionDays: Flow<Int> = context.dataStore.data.map { it[Keys.LOCATION_HISTORY_RETENTION_DAYS] ?: 90 }
    suspend fun setLocationHistoryRetentionDays(days: Int) {
        context.dataStore.edit { it[Keys.LOCATION_HISTORY_RETENTION_DAYS] = days }
    }

    val onboardingComplete: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_COMPLETE] ?: false }
    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }
}
