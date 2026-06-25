package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val token: String,
    val user: UserData,
    val message: String? = null
)

data class UserData(
    val id: Any, // Can be Int or String from API
    @SerializedName("fullName")
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val profileImage: String? = null,
    val phone: String? = null
) {
    val derivedUsername: String get() = username ?: email
    val derivedFirstName: String get() = firstName ?: fullName.split(" ").firstOrNull() ?: ""
    val derivedLastName: String get() = lastName ?: fullName.split(" ").lastOrNull() ?: ""
}