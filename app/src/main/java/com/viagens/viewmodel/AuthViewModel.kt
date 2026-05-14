package com.viagens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viagens.data.local.database.AppDatabase
import com.viagens.data.local.entity.User
import com.viagens.data.local.session.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase
        .getDatabase(application)
        .userDao()

    private val sessionManager = SessionManager(application)

    fun registerUser(
        nome: String,
        email: String,
        senha: String,
        telefone: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {

            val existingUser = userDao.getUserByEmail(email)

            if (existingUser != null) {
                onError("Este email já está cadastrado")
                return@launch
            }

            if (!email.contains("@")) {
                onError("Email inválido")
                return@launch
            }

            val user = User(
                name = nome,
                email = email,
                password = senha,
                phone = telefone
            )

            userDao.insert(user)

            onSuccess()
        }
    }

    fun login(email: String, senha: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userDao.login(email, senha)

            if (user != null) {
                sessionManager.saveUser(email) // 💾 salva sessão
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun getLoggedUser(): String? {
        return sessionManager.getUser()
    }

    fun logout() {
        sessionManager.logout()
    }
}
