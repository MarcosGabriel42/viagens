package com.viagens.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Menu : Screen("menu")
    object NewTrip : Screen("new_trip?tripId={tripId}") {
        fun createRoute(tripId: Int? = null) = if (tripId != null) "new_trip?tripId=$tripId" else "new_trip"
    }
    object MyTrips : Screen("my_trips")
    object About : Screen("about")
    object TripDetails : Screen("trip_details/{tripId}") {
        fun createRoute(tripId: Int) = "trip_details/$tripId"
    }
    object PhotoViewer : Screen("photo_viewer/{tripId}/{photoId}") {
        fun createRoute(tripId: Int, photoId: Int) = "photo_viewer/$tripId/$photoId"
    }
}
