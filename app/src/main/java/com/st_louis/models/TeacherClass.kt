package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class TeacherClass(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("subject")
    val subject: String,

    @SerializedName("time")
    val time: String,

    @SerializedName("room")
    val room: String,

    @SerializedName("students_count")
    val studentsCount: Int,

    @SerializedName("section")
    val section: String? = null, // "Pre-Primary" or "Primary"

    @SerializedName("level")
    val level: String? = null, // e.g., "Grade 4", "PP1", etc.

    @SerializedName("day")
    val day: String? = null, // Monday, Tuesday, etc.

    @SerializedName("duration")
    val duration: String? = null, // e.g., "45 mins"

    @SerializedName("teacher_name")
    val teacherName: String? = null,

    @SerializedName("teacher_id")
    val teacherId: String? = null,

    @SerializedName("term")
    val term: String? = null,

    @SerializedName("year")
    val year: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true
)

// For class details with more information
data class ClassDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("section")
    val section: String,

    @SerializedName("level")
    val level: String,

    @SerializedName("teacher_id")
    val teacherId: String,

    @SerializedName("teacher_name")
    val teacherName: String,

    @SerializedName("subjects")
    val subjects: List<Subject>,

    @SerializedName("students")
    val students: List<Student>,

    @SerializedName("total_students")
    val totalStudents: Int,

    @SerializedName("schedule")
    val schedule: List<ClassSchedule>,

    @SerializedName("term")
    val term: String,

    @SerializedName("year")
    val year: String,

    @SerializedName("is_active")
    val isActive: Boolean
)

// For class schedule/timetable
data class ClassSchedule(
    @SerializedName("day")
    val day: String,

    @SerializedName("start_time")
    val startTime: String,

    @SerializedName("end_time")
    val endTime: String,

    @SerializedName("subject")
    val subject: String,

    @SerializedName("room")
    val room: String,

    @SerializedName("teacher_id")
    val teacherId: String? = null,

    @SerializedName("teacher_name")
    val teacherName: String? = null
)

// For subject details
data class Subjects(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("section")
    val section: String? = null, // "Pre-Primary" or "Primary"

    @SerializedName("teacher_id")
    val teacherId: String? = null,

    @SerializedName("teacher_name")
    val teacherName: String? = null
)

// For class list response
data class ClassListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<TeacherClass>? = null
)

// For class summary
data class ClassSummary(
    @SerializedName("total_classes")
    val totalClasses: Int,

    @SerializedName("pre_primary_classes")
    val prePrimaryClasses: Int,

    @SerializedName("primary_classes")
    val primaryClasses: Int,

    @SerializedName("total_students")
    val totalStudents: Int,

    @SerializedName("average_class_size")
    val averageClassSize: Double,

    @SerializedName("classes")
    val classes: List<TeacherClass>? = null
)

// For class filter
data class ClassFilter(
    @SerializedName("section")
    val section: String? = null,

    @SerializedName("level")
    val level: String? = null,

    @SerializedName("term")
    val term: String? = null,

    @SerializedName("year")
    val year: String? = null,

    @SerializedName("teacher_id")
    val teacherId: String? = null
)