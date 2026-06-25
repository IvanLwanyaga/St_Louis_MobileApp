package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class AttendanceSummary(
    @SerializedName("total_students")
    val totalStudents: Int,

    @SerializedName("present")
    val present: Int,

    @SerializedName("absent")
    val absent: Int,

    @SerializedName("not_marked")
    val notMarked: Int,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("class_name")
    val className: String? = null,

    @SerializedName("section")
    val section: String? = null,

    @SerializedName("attendance_rate")
    val attendanceRate: Double? = null
)