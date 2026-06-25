package com.st_louis.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val context: Context
) {
    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    fun saveUser(
        id: String,
        username: String,
        email: String,
        role: String,
        token: String,
        firstName: String,
        lastName: String
    ) {
        sharedPref.edit().apply {
            putString("user_id", id)
            putString("username", username)
            putString("email", email)
            putString("role", role)
            putString("token", token)
            putString("first_name", firstName)
            putString("last_name", lastName)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun getUser(): Map<String, String?> {
        return mapOf(
            "id" to sharedPref.getString("user_id", null),
            "username" to sharedPref.getString("username", null),
            "email" to sharedPref.getString("email", null),
            "role" to sharedPref.getString("role", null),
            "token" to sharedPref.getString("token", null),
            "firstName" to sharedPref.getString("first_name", null),
            "lastName" to sharedPref.getString("last_name", null)
        )
    }

    fun getToken(): String? {
        return sharedPref.getString("token", null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean("is_logged_in", false)
    }

    fun clearSession() {
        sharedPref.edit().clear().apply()
    }

    fun logout() {
        clearSession()
    }
}