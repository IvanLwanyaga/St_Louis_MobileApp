package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class AttendanceRequest(
    @SerializedName("teacher_id")
    val teacherId: String,

    @SerializedName("class_id")
    val classId: String,

    @SerializedName("class_name")
    val className: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("section")
    val section: String,

    @SerializedName("term")
    val term: String? = null,

    @SerializedName("year")
    val year: String? = null,

    @SerializedName("attendance")
    val attendance: List<AttendanceRecord>
)