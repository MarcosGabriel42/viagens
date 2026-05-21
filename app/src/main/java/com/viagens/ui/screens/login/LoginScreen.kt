package com.viagens.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viagens.ui.components.CustomTextField
import com.viagens.ui.components.PrimaryButton
import com.viagens.ui.components.SecondaryButton
import com.viagens.ui.components.WaveHeader
import com.viagens.ui.navigation.Screen
import com.viagens.ui.theme.TextDark
import com.viagens.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WaveHeader(title = "Bem-vindo")

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Faça login para continuar",
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(32.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "E-mail",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = "Senha",
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.navigate(Screen.ForgotPassword.route) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Esqueceu a senha?", color = TextDark)
                }

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = "ENTRAR",
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
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SecondaryButton(
                    text = "CRIAR CONTA",
                    onClick = { navController.navigate(Screen.Register.route) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
