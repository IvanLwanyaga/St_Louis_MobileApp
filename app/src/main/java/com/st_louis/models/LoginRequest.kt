package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val username: String,
    val password: String,
    val role: String
)