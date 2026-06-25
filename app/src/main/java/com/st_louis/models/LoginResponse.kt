package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: LoginData?
)

data class LoginData(
    @SerializedName("user")
    val user: User,

    @SerializedName("token")
    val token: String
)