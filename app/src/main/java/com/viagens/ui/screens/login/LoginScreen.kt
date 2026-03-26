package com.viagens.ui.screens.login

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.viagens.ui.components.CustomTextField
import com.viagens.ui.navigation.Screen
import com.viagens.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = AuthViewModel()
) {

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(email, { email = it }, "E-mail")

        Spacer(modifier = Modifier.height(10.dp))

        CustomTextField(senha, { senha = it }, "Senha")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (viewModel.login(email, senha)) {
                    navController.navigate(Screen.Menu.route)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        TextButton(onClick = {
            navController.navigate(Screen.Register.route)
        }) {
            Text("Criar conta")
        }

        TextButton(onClick = {
            navController.navigate(Screen.ForgotPassword.route)
        }) {
            Text("Esqueci a senha")
        }
    }
}