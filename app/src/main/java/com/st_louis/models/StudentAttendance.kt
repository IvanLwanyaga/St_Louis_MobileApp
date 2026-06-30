package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class StudentAttendance(
    @SerializedName("student_id")
    val studentId: String,

    @SerializedName("student_name")
    val studentName: String,

    @SerializedName("admission_no")
    val admissionNo: String,

    @SerializedName("class_name")
    val className: String,

    @SerializedName("total_days")
    val totalDays: Int,

    @SerializedName("present")
    val present: Int,

    @SerializedName("absent")
    val absent: Int,

    @SerializedName("late")
    val late: Int,

    @SerializedName("excused")
    val excused: Int,

    @SerializedName("attendance_rate")
    val attendanceRate: Double,

    @SerializedName("records")
    val records: List<AttendanceRecord>
)