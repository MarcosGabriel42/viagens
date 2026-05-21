package com.viagens.ui.screens.login

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viagens.ui.components.CustomTextField
import com.viagens.ui.components.AppLogo
import com.viagens.ui.navigation.Screen
import com.viagens.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController) {

    val viewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🎸 LOGO
        AppLogo()

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 📧 Email
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "E-mail",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 🔒 Senha com olhinho
        CustomTextField(
            value = senha,
            onValueChange = { senha = it },
            label = "Senha",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && senha.isNotEmpty()) {
                    viewModel.login(email, senha) { sucesso ->
                        if (sucesso) {
                            navController.navigate(Screen.Menu.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(10.dp))

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
