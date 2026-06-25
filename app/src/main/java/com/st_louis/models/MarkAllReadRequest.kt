package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class MarkAllReadRequest(
    @SerializedName("teacher_id")
    val teacherId: String
)