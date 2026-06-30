package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class AttendanceExport(
    @SerializedName("url")
    val url: String,

    @SerializedName("file_name")
    val fileName: String,

    @SerializedName("format")
    val format: String,

    @SerializedName("generated_at")
    val generatedAt: String
)