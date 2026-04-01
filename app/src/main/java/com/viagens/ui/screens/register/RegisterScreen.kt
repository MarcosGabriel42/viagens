package com.viagens.ui.screens.register

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import com.viagens.ui.navigation.Screen

@Composable
fun RegisterScreen(navController: NavController) {

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

        OutlinedTextField(nome, { nome = it }, label = { Text("Nome") }, modifier = Modifier
            .fillMaxWidth())
        OutlinedTextField(email, { email = it }, label = { Text("E-mail") }, modifier = Modifier
            .fillMaxWidth())
        OutlinedTextField(telefone, { telefone = it }, label = { Text("Telefone") }, modifier = Modifier
            .fillMaxWidth())
        OutlinedTextField(senha, { senha = it }, label = { Text("Senha") },  modifier = Modifier
            .fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (
                    nome.isNotEmpty() &&
                    email.isNotEmpty() &&
                    telefone.isNotEmpty() &&
                    senha == confirmarSenha
                ) {
                    navController.navigate(Screen.Login.route)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }
    }}
}