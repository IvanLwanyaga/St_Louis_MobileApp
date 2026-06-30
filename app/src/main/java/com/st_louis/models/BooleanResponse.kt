package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class BooleanResponse(
    @SerializedName("exists")
    val exists: Boolean,

    @SerializedName("available")
    val available: Boolean? = null,

    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("message")
    val message: String? = null
)