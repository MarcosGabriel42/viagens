package com.viagens.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.viagens.ui.screens.login.LoginScreen
import com.viagens.ui.screens.register.RegisterScreen
import com.viagens.ui.screens.forgotpassword.ForgotPasswordScreen
import com.viagens.ui.screens.menu.MenuScreen
import com.viagens.ui.screens.newtrip.NewTripScreen
import com.viagens.ui.screens.mytrips.MyTripsScreen
import com.viagens.ui.screens.about.AboutScreen
import com.viagens.ui.screens.details.TripDetailsScreen
import com.viagens.ui.screens.details.PhotoViewerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }

        composable(Screen.Menu.route) {
            MenuScreen(navController)
        }

        composable(
            route = Screen.NewTrip.route,
            arguments = listOf(navArgument("tripId") { 
                type = NavType.IntType
                defaultValue = -1 
            })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: -1
            NewTripScreen(navController, if (tripId == -1) null else tripId)
        }

        composable(Screen.MyTrips.route) {
            MyTripsScreen(navController)
        }

        composable(Screen.About.route) {
            AboutScreen(navController)
        }

        composable(
            route = Screen.TripDetails.route,
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: -1
            TripDetailsScreen(navController, tripId)
        }

        composable(
            route = Screen.PhotoViewer.route,
            arguments = listOf(
                navArgument("tripId") { type = NavType.IntType },
                navArgument("photoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: -1
            val photoId = backStackEntry.arguments?.getInt("photoId") ?: -1
            PhotoViewerScreen(navController, tripId, photoId)
        }
    }
}
