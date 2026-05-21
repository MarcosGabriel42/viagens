package com.viagens.ui.screens.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

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
            WaveHeader(title = "Criar Conta")

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Preencha os dados abaixo",
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = "Nome Completo",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "E-mail",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = telefone,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 11) {
                            telefone = digits
                        }
                    },
                    label = "Telefone (com DDD)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = confirmarSenha,
                    onValueChange = { confirmarSenha = it },
                    label = "Confirmar Senha",
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = "CADASTRAR",
                    onClick = {
                        when {
                            nome.isEmpty() || email.isEmpty() || telefone.length < 10 -> {
                                Toast.makeText(context, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
                            }
                            !email.contains("@") -> {
                                Toast.makeText(context, "Email inválido", Toast.LENGTH_SHORT).show()
                            }
                            senha != confirmarSenha -> {
                                Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                viewModel.registerUser(
                                    nome = nome,
                                    email = email,
                                    senha = senha,
                                    telefone = telefone,
                                    onSuccess = {
                                        Toast.makeText(context, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                                        navController.navigate(Screen.Login.route)
                                    },
                                    onError = { erro ->
                                        Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SecondaryButton(
                    text = "VOLTAR AO LOGIN",
                    onClick = { navController.popBackStack() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
