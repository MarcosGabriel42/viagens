package com.viagens.viewmodel

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    fun login(email: String, senha: String): Boolean {
        return email.isNotEmpty() && senha.isNotEmpty()
    }

}