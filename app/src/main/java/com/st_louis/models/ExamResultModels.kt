package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class ExamPeriod(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String // e.g. "Term 1 — End of Term Exams"
)

data class SubjectResult(
    @SerializedName("subject_name")
    val subjectName: String,
    @SerializedName("marks")
    val marks: Int,
    @SerializedName("total_marks")
    val totalMarks: Int = 100,
    @SerializedName("grade")
    val grade: String
)

data class AcademicResultsResponse(
    @SerializedName("student_name")
    val studentName: String,
    @SerializedName("student_class")
    val studentClass: String,
    @SerializedName("academic_year")
    val academicYear: String,
    @SerializedName("overall_average")
    val overallAverage: Double,
    @SerializedName("overall_grade")
    val overallGrade: String,
    @SerializedName("performance_comment")
    val performanceComment: String,
    @SerializedName("class_position")
    val classPosition: Int,
    @SerializedName("total_students")
    val totalStudents: Int,
    @SerializedName("results")
    val results: List<SubjectResult>
)
