package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("expires_in")
    val expiresIn: Int? = null,

    @SerializedName("token_type")
    val tokenType: String? = null
)