package com.st_louis.models

data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val firstName: String,
    val lastName: String,
    val token: String,
    val profileImage: String? = null,
    val phone: String? = null,
    val isActive: Boolean = true
)