package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class Exam(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("term")
    val term: String,

    @SerializedName("year")
    val year: String,

    @SerializedName("start_date")
    val startDate: String? = null,

    @SerializedName("end_date")
    val endDate: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true
)