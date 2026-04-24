package com.viagens.ui.screens.register

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.viagens.ui.navigation.Screen
import com.viagens.viewmodel.AuthViewModel

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("Cadastro", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefone,
                onValueChange = { telefone = it },
                label = { Text("Telefone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it },
                label = { Text("Confirmar Senha") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (
                        nome.isNotEmpty() &&
                        email.isNotEmpty() &&
                        telefone.isNotEmpty() &&
                        senha == confirmarSenha
                    ) {
                        viewModel.registerUser(
                            nome = nome,
                            email = email,
                            senha = senha,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Usuário cadastrado com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate(Screen.Login.route)
                            }
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Preencha os campos corretamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cadastrar")
            }
        }
    }
}