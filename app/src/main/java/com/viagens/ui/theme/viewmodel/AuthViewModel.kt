package com.viagens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viagens.data.local.database.AppDatabase
import com.viagens.data.local.entity.User
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase
        .getDatabase(application)
        .userDao()

    fun registerUser(
        nome: String,
        email: String,
        senha: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = User(
                name = nome,
                email = email,
                password = senha
            )

            userDao.insert(user)

            onSuccess()
        }
    }

    fun login(email: String, senha: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userDao.login(email, senha)
            onResult(user != null)
        }
    }
}