package com.orbit.spatialjournal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.orbit.spatialjournal.ui.screens.home.HomeScreen
import com.orbit.spatialjournal.ui.screens.map.MapScreen
import com.orbit.spatialjournal.ui.screens.timeline.TimelineScreen
import com.orbit.spatialjournal.ui.screens.memories.MemoriesScreen
import com.orbit.spatialjournal.ui.screens.memories.MemoryDetailScreen
import com.orbit.spatialjournal.ui.screens.memories.CaptureScreen
import com.orbit.spatialjournal.ui.screens.trips.TripsScreen
import com.orbit.spatialjournal.ui.screens.trips.TripDetailScreen
import com.orbit.spatialjournal.ui.screens.journal.JournalEditorScreen
import com.orbit.spatialjournal.ui.screens.places.PlaceDetailScreen
import com.orbit.spatialjournal.ui.screens.search.SearchScreen
import com.orbit.spatialjournal.ui.screens.settings.SettingsScreen
import com.orbit.spatialjournal.ui.screens.settings.PrivacyCenterScreen
import com.orbit.spatialjournal.ui.screens.settings.LocationHistoryScreen
import com.orbit.spatialjournal.ui.screens.settings.BackupScreen
import com.orbit.spatialjournal.ui.screens.graph.GraphScreen

@Composable
fun OrbitNavHost(navController: NavHostController, startDestination: String = Destination.Home.route) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Destination.Home.route) { HomeScreen(navController) }
        composable(Destination.Map.route) { MapScreen(navController) }
        composable(Destination.Timeline.route) { TimelineScreen(navController) }
        composable(Destination.Memories.route) { MemoriesScreen(navController) }
        composable(Destination.Trips.route) { TripsScreen(navController) }
        composable(Destination.Settings.route) { SettingsScreen(navController) }
        composable(Destination.Search.route) { SearchScreen(navController) }
        composable(Destination.PrivacyCenter.route) { PrivacyCenterScreen(navController) }
        composable(Destination.LocationHistory.route) { LocationHistoryScreen(navController) }
        composable(Destination.Backup.route) { BackupScreen(navController) }
        composable(Destination.Graph.route) { GraphScreen(navController) }

        composable(
            route = Destination.Capture.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            CaptureScreen(navController, backStackEntry.arguments?.getString("type").orEmpty())
        }

        composable(
            route = Destination.MemoryDetail.route,
            arguments = listOf(navArgument("memoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            MemoryDetailScreen(navController, backStackEntry.arguments?.getString("memoryId").orEmpty())
        }

        composable(
            route = Destination.JournalEditor.route,
            arguments = listOf(navArgument("memoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            JournalEditorScreen(navController, backStackEntry.arguments?.getString("memoryId").orEmpty())
        }

        composable(
            route = Destination.TripDetail.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            TripDetailScreen(navController, backStackEntry.arguments?.getString("tripId").orEmpty())
        }

        composable(
            route = Destination.PlaceDetail.route,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            PlaceDetailScreen(navController, backStackEntry.arguments?.getString("placeId").orEmpty())
        }
    }
}
