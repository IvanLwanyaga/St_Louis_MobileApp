package com.st_louis.models

import com.google.gson.annotations.SerializedName

// Generic API Response
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null,

    @SerializedName("status_code")
    val statusCode: Int? = null
)

// For responses without data
data class SimpleApiResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("status_code")
    val statusCode: Int? = null
)