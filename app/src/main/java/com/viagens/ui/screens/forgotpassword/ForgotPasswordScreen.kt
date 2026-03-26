package com.viagens.ui.screens.forgotpassword

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.viagens.ui.navigation.Screen

@Composable
fun ForgotPasswordScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Recuperar Senha", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Digite seu e-mail") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Login.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }
    }
}