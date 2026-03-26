package com.viagens.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },

        visualTransformation =
            if (isPassword && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,

        trailingIcon = {
            if (isPassword) {
                val icon = if (passwordVisible)
                    Icons.Default.Visibility
                else
                    Icons.Default.VisibilityOff

                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Toggle Password"
                    )
                }
            }
        }
    )
}