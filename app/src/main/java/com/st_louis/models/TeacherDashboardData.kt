package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class TeacherDashboardData(
    @SerializedName("total_students")
    val totalStudents: Int,

    @SerializedName("marked_attendance")
    val markedAttendance: Int,

    @SerializedName("unmarked_attendance")
    val unmarkedAttendance: Int,

    @SerializedName("pending_tasks")
    val pendingTasks: List<String>,

    @SerializedName("class_name")
    val className: String? = null,

    @SerializedName("today_classes")
    val todayClasses: List<TeacherClass>? = null,

    @SerializedName("total_classes_today")
    val totalClassesToday: Int? = null,

    @SerializedName("total_subjects")
    val totalSubjects: Int? = null,

    @SerializedName("attendance_rate")
    val attendanceRate: Double? = null
)