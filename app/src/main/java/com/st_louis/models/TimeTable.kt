package com.st_louis.models

import com.google.gson.annotations.SerializedName

// ==================== PRIMARY TIMETABLE MODELS ====================

data class PrimaryTimetable(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("class_name")
    val className: String, // "P4-A", "P4-B", "P5-A", etc.

    @SerializedName("term")
    val term: String, // "Term 1", "Term 2", "Term 3"

    @SerializedName("year")
    val year: String, // "2025/26"

    @SerializedName("periods_per_week")
    val periodsPerWeek: Int,

    @SerializedName("subjects")
    val subjects: List<Subject>,

    @SerializedName("teachers")
    val teachers: List<TeacherAssignment>,

    @SerializedName("conflicts")
    val conflicts: List<TimetableConflict>
)

data class Subject(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String, // "Mathematics", "English", etc.

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("color")
    val color: String? = null // For UI highlighting
)

data class TeacherAssignment(
    @SerializedName("teacher_id")
    val teacherId: Int,

    @SerializedName("teacher_name")
    val teacherName: String,

    @SerializedName("subject")
    val subject: String,

    @SerializedName("is_shared")
    val isShared: Boolean = false,

    @SerializedName("shared_with")
    val sharedWith: String? = null
)

data class TimetableConflict(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("type")
    val type: String, // "room_clash", "teacher_clash", "double_booking"

    @SerializedName("description")
    val description: String,

    @SerializedName("day")
    val day: String,

    @SerializedName("time")
    val time: String,

    @SerializedName("severity")
    val severity: String = "high" // "high", "medium", "low"
)

data class TimetablePeriod(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("day")
    val day: String, // "Monday", "Tuesday", etc.

    @SerializedName("time")
    val time: String, // "7:30", "8:30", etc.

    @SerializedName("subject")
    val subject: String,

    @SerializedName("teacher")
    val teacher: String,

    @SerializedName("room")
    val room: String,

    @SerializedName("class_name")
    val className: String,

    @SerializedName("has_conflict")
    val hasConflict: Boolean = false,

    @SerializedName("conflict_reason")
    val conflictReason: String? = null
)

data class PrimaryTimetableResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: PrimaryTimetableData? = null
)

data class PrimaryTimetableData(
    @SerializedName("timetable")
    val timetable: PrimaryTimetable,

    @SerializedName("periods")
    val periods: List<TimetablePeriod>,

    @SerializedName("stats")
    val stats: PrimaryTimetableStats
)

data class PrimaryTimetableStats(
    @SerializedName("periods_per_week")
    val periodsPerWeek: Int,

    @SerializedName("subjects")
    val subjects: Int,

    @SerializedName("teachers_assigned")
    val teachersAssigned: Int,

    @SerializedName("teachers_shared")
    val teachersShared: Int,

    @SerializedName("conflicts")
    val conflicts: Int
)

// ==================== PRE-PRIMARY TIMETABLE MODELS ====================

data class PrePrimaryTimetable(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("class_name")
    val className: String, // "Baby Class", "Nursery", "Reception"

    @SerializedName("age_range")
    val ageRange: String, // "2-3 yrs", "3-4 yrs", "4-5 yrs"

    @SerializedName("student_count")
    val studentCount: Int,

    @SerializedName("term")
    val term: String,

    @SerializedName("year")
    val year: String,

    @SerializedName("caregivers")
    val caregivers: Int,

    @SerializedName("assistant_count")
    val assistantCount: Int,

    @SerializedName("school_hours")
    val schoolHours: String, // "7h 30m"

    @SerializedName("nap_included")
    val napIncluded: Boolean = false,

    @SerializedName("nap_time")
    val napTime: String? = null
)

data class PrePrimaryActivity(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("time")
    val time: String, // "7:30", "8:00", etc.

    @SerializedName("duration")
    val duration: String, // "30 min", "60 min", etc.

    @SerializedName("activity_name")
    val activityName: String, // "Arrival & settling", "Guided play", etc.

    @SerializedName("description")
    val description: String,

    @SerializedName("type")
    val type: String, // "group", "individual", "class"

    @SerializedName("class_name")
    val className: String,

    @SerializedName("day")
    val day: String? = null // "Monday", "Tuesday", etc. (null if daily routine)
)

data class PrePrimaryRoutine(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("class_name")
    val className: String,

    @SerializedName("day")
    val day: String, // "Monday", "Tuesday", etc.

    @SerializedName("activities")
    val activities: List<PrePrimaryActivity>
)

data class PrePrimaryTimetableResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: PrePrimaryTimetableData? = null
)

data class PrePrimaryTimetableData(
    @SerializedName("timetable")
    val timetable: PrePrimaryTimetable,

    @SerializedName("routines")
    val routines: List<PrePrimaryRoutine>,

    @SerializedName("activities")
    val activities: List<PrePrimaryActivity>,

    @SerializedName("stats")
    val stats: PrePrimaryStats
)

data class PrePrimaryStats(
    @SerializedName("total_activities")
    val totalActivities: Int,

    @SerializedName("school_hours")
    val schoolHours: String,

    @SerializedName("caregivers")
    val caregivers: Int,

    @SerializedName("nap_included")
    val napIncluded: Boolean
)

// ==================== COMMON TIMETABLE MODELS ====================

data class ClassInfo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("section")
    val section: String, // "primary" or "pre-primary"

    @SerializedName("level")
    val level: String? = null // "P4", "P5", etc. for primary
)

data class TimetableSummary(
    @SerializedName("total_classes")
    val totalClasses: Int,

    @SerializedName("primary_classes")
    val primaryClasses: Int,

    @SerializedName("pre_primary_classes")
    val prePrimaryClasses: Int,

    @SerializedName("total_conflicts")
    val totalConflicts: Int
)