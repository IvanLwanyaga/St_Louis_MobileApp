package com.st_louis.models

import com.google.gson.annotations.SerializedName

// Admin Stats
data class AdminStats(
    @SerializedName("total_students")
    val totalStudents: String,

    @SerializedName("total_staff")
    val totalStaff: String,

    @SerializedName("attendance_rate")
    val attendanceRate: String,

    @SerializedName("fees_collected")
    val feesCollected: String,

    @SerializedName("total_classes")
    val totalClasses: String? = null,

    @SerializedName("total_teachers")
    val totalTeachers: String? = null,

    @SerializedName("total_parents")
    val totalParents: String? = null
)

// Teacher Stats
data class TeacherStats(
    @SerializedName("classes_today")
    val classesToday: String,

    @SerializedName("total_students")
    val totalStudents: String,

    @SerializedName("avg_score")
    val avgScore: String,

    @SerializedName("total_classes")
    val totalClasses: String? = null,

    @SerializedName("subjects_taught")
    val subjectsTaught: String? = null,

    @SerializedName("attendance_rate")
    val attendanceRate: String? = null
)

// Student Stats
data class StudentStats(
    @SerializedName("rank")
    val rank: String,

    @SerializedName("avg_score")
    val avgScore: String,

    @SerializedName("total_subjects")
    val totalSubjects: String? = null,

    @SerializedName("attendance_rate")
    val attendanceRate: String? = null,

    @SerializedName("grade")
    val grade: String? = null,

    @SerializedName("class_position")
    val classPosition: String? = null
)

// Parent Stats
data class ParentStats(
    @SerializedName("child_attendance")
    val childAttendance: String,

    @SerializedName("fee_balance")
    val feeBalance: String,

    @SerializedName("total_children")
    val totalChildren: String? = null,

    @SerializedName("children_performance")
    val childrenPerformance: String? = null,

    @SerializedName("upcoming_events")
    val upcomingEvents: String? = null
)

// Bursar Stats
data class BursarStats(
    @SerializedName("today_collection")
    val todayCollection: String,

    @SerializedName("pending_fees")
    val pendingFees: String,

    @SerializedName("total_collected")
    val totalCollected: String? = null,

    @SerializedName("fee_collection_rate")
    val feeCollectionRate: String? = null,

    @SerializedName("outstanding_balance")
    val outstandingBalance: String? = null
)