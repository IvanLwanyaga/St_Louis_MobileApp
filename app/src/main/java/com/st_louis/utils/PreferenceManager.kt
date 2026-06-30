package com.st_louis.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.st_louis.models.User
import com.st_louis.models.UserRole

class PreferenceManager private constructor(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "StLouisPrefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER = "current_user"
        private const val KEY_USER_ROLE = "user_role"

        @Volatile
        private var INSTANCE: PreferenceManager? = null

        fun getInstance(context: Context): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceManager(context).also { INSTANCE = it }
            }
        }
    }

    fun saveToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        preferences.edit().putString(KEY_USER, userJson).apply()
        saveUserRole(user.role.name)
    }

    fun getCurrentUser(): User? {
        val userJson = preferences.getString(KEY_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }

    fun saveUserRole(role: String) {
        preferences.edit().putString(KEY_USER_ROLE, role).apply()
    }

    fun getUserRole(): String? {
        return preferences.getString(KEY_USER_ROLE, null)
    }

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}