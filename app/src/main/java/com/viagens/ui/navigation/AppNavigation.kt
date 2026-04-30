package com.viagens.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.viagens.ui.screens.login.LoginScreen
import com.viagens.ui.screens.register.RegisterScreen
import com.viagens.ui.screens.forgotpassword.ForgotPasswordScreen
import com.viagens.ui.screens.menu.MenuScreen

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
    }
}
