package com.viagens.ui.screens.register

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.KeyboardType
import com.viagens.ui.navigation.Screen
import com.viagens.viewmodel.AuthViewModel
import com.viagens.ui.components.CustomTextField
import com.viagens.ui.components.AppLogo

@Composable
fun RegisterScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔥 Logo reutilizada
            AppLogo()

            Text(
                text = "Criar Conta",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 👤 Nome
            CustomTextField(
                value = nome,
                onValueChange = { nome = it },
                label = "Nome Completo",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 📧 Email
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-mail",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 📱 Telefone (somente números)
            OutlinedTextField(
                value = telefone,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }

                    if (digits.length <= 11) {
                        telefone = digits
                    }
                },
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🔒 Senha
            CustomTextField(
                value = senha,
                onValueChange = { senha = it },
                label = "Senha",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🔒 Confirmar Senha
            CustomTextField(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it },
                label = "Confirmar Senha",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Botão cadastrar
            Button(
                onClick = {

                    when {
                        nome.isEmpty() ||
                                email.isEmpty() ||
                                telefone.length != 11 -> {
                            Toast.makeText(
                                context,
                                "Preencha todos os campos corretamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        !email.contains("@") -> {
                            Toast.makeText(
                                context,
                                "Email inválido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        senha != confirmarSenha -> {
                            Toast.makeText(
                                context,
                                "As senhas não coincidem",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            viewModel.registerUser(
                                nome = nome,
                                email = email,
                                senha = senha,
                                telefone = telefone, // 🔥 só números
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.navigate(Screen.Login.route)
                                },
                                onError = { erro ->
                                    Toast.makeText(
                                        context,
                                        erro,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cadastrar")
            }
        }
    }
}