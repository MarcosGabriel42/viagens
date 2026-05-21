package com.viagens.ui.screens.forgotpassword

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
import androidx.navigation.NavController
import com.viagens.ui.components.CustomTextField
import com.viagens.ui.components.PrimaryButton
import com.viagens.ui.components.SecondaryButton
import com.viagens.ui.components.WaveHeader
import com.viagens.ui.theme.TextDark

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

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
            WaveHeader(title = "Recuperar\nSenha")

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Insira seu e-mail para receber as instruções de recuperação.",
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(32.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "E-mail cadastrado",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(48.dp))

                PrimaryButton(
                    text = "ENVIAR INSTRUÇÕES",
                    onClick = {
                        if (email.isNotEmpty() && email.contains("@")) {
                            Toast.makeText(context, "E-mail enviado!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Insira um e-mail válido", Toast.LENGTH_SHORT).show()
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
