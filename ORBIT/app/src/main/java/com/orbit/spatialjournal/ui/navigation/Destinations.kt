package com.orbit.spatialjournal.ui.navigation

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object Map : Destination("map")
    data object Timeline : Destination("timeline")
    data object Memories : Destination("memories")
    data object Trips : Destination("trips")
    data object Settings : Destination("settings")

    data object Capture : Destination("capture?type={type}") {
        fun withType(type: String?) = if (type != null) "capture?type=$type" else "capture?type="
    }
    data object MemoryDetail : Destination("memory/{memoryId}") {
        fun with(memoryId: String) = "memory/$memoryId"
    }
    data object JournalEditor : Destination("journal/{memoryId}") {
        fun with(memoryId: String) = "journal/$memoryId"
    }
    data object TripDetail : Destination("trip/{tripId}") {
        fun with(tripId: String) = "trip/$tripId"
    }
    data object PlaceDetail : Destination("place/{placeId}") {
        fun with(placeId: String) = "place/$placeId"
    }
    data object Search : Destination("search")
    data object PrivacyCenter : Destination("privacy_center")
    data object LocationHistory : Destination("location_history")
    data object Backup : Destination("backup")
    data object Graph : Destination("graph")
}

val bottomNavDestinations = listOf(
    Destination.Home, Destination.Map, Destination.Timeline, Destination.Memories, Destination.Trips
)
