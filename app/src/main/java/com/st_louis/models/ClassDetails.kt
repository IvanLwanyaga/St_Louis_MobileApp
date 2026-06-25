package com.st_louis.models

// ClassDetail.kt
data class ClassDetails(
    val id: String,
    val name: String,
    val section: String,
    val level: String,
    val teacherId: String,
    val teacherName: String,
    val subjects: List<Subject>,
    val students: List<Student>,
    val totalStudents: Int,
    val schedule: List<ClassSchedule>,
    val term: String,
    val year: String,
    val isActive: Boolean
)

// ClassSummary.kt
data class ClassSummarys(
    val totalClasses: Int,
    val prePrimaryClasses: Int,
    val primaryClasses: Int,
    val totalStudents: Int,
    val averageClassSize: Double,
    val classes: List<TeacherClass>? = null
)