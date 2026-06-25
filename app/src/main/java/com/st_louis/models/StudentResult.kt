package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class StudentResult(
    @SerializedName("student_id")
    val studentId: String,

    @SerializedName("student_name")
    val studentName: String,

    @SerializedName("admission_no")
    val admissionNo: String,

    @SerializedName("score")
    val score: Int,

    @SerializedName("grade")
    val grade: String,

    @SerializedName("subject")
    val subject: String,

    @SerializedName("exam_type")
    val examType: String,

    @SerializedName("class_name")
    val className: String? = null,

    @SerializedName("term")
    val term: String? = null,

    @SerializedName("year")
    val year: String? = null
)