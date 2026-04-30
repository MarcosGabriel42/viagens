package com.viagens.data.local.session

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun getUser(): String? {
        return prefs.getString("user_email", null)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}