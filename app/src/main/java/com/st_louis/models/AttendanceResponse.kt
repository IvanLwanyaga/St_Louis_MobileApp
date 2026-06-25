package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class AttendanceResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AttendanceData? = null
)

data class AttendanceData(
    @SerializedName("attendance_id")
    val attendanceId: String,

    @SerializedName("class_name")
    val className: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("total_students")
    val totalStudents: Int,

    @SerializedName("present_count")
    val presentCount: Int,

    @SerializedName("absent_count")
    val absentCount: Int,

    @SerializedName("late_count")
    val lateCount: Int,

    @SerializedName("excused_count")
    val excusedCount: Int,

    @SerializedName("attendance_rate")
    val attendanceRate: Double,

    @SerializedName("records")
    val records: List<AttendanceRecord>
)