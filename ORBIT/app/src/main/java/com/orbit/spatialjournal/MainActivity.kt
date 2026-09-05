package com.orbit.spatialjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orbit.spatialjournal.core.model.AppLanguage
import com.orbit.spatialjournal.ui.navigation.Destination
import com.orbit.spatialjournal.ui.navigation.OrbitNavHost
import com.orbit.spatialjournal.ui.navigation.bottomNavDestinations
import com.orbit.spatialjournal.ui.screens.settings.SettingsViewModel
import com.orbit.spatialjournal.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.uiState.collectAsState()

            // Persian renders fully right-to-left; English/Chinese are left-to-right — both
            // axes are handled by Compose's LocalLayoutDirection, which every Material3
            // component (including this NavigationBar and every screen) mirrors automatically.
            val layoutDirection = if (settings.language == AppLanguage.PERSIAN) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                OrbitTheme(themeMode = settings.themeMode, accentStyle = settings.accentStyle) {
                    OrbitApp()
                }
            }
        }
    }
}

@Composable
private fun OrbitApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(iconFor(destination.route), contentDescription = destination.route) },
                        label = { Text(labelFor(destination.route)) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            OrbitNavHost(navController = navController)
        }
    }
}

private fun iconFor(route: String) = when (route) {
    Destination.Home.route -> Icons.Filled.Home
    Destination.Map.route -> Icons.Filled.Map
    Destination.Timeline.route -> Icons.Filled.Timeline
    Destination.Memories.route -> Icons.Filled.PhotoLibrary
    Destination.Trips.route -> Icons.Filled.Flight
    else -> Icons.Filled.Circle
}

private fun labelFor(route: String) = when (route) {
    Destination.Home.route -> "Home"
    Destination.Map.route -> "Map"
    Destination.Timeline.route -> "Timeline"
    Destination.Memories.route -> "Memories"
    Destination.Trips.route -> "Trips"
    else -> route
}
