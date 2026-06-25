package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class ResultsRequest(
    @SerializedName("teacher_id")
    val teacher_id: String,

    @SerializedName("class_id")
    val class_id: String,

    @SerializedName("exam_id")
    val exam_id: String,

    @SerializedName("subject")
    val subject: String,

    @SerializedName("section")
    val section: String,

    @SerializedName("term")
    val term: String,

    @SerializedName("year")
    val year: String,

    @SerializedName("results")
    val results: List<ResultRecord>
)

data class ResultRecord(
    @SerializedName("student_id")
    val student_id: String,

    @SerializedName("score")
    val score: Int,

    @SerializedName("grade")
    val grade: String,

    @SerializedName("remarks")
    val remarks: String? = null
)

data class ResultUpdateRequest(
    @SerializedName("score")
    val score: Int,

    @SerializedName("grade")
    val grade: String,

    @SerializedName("remarks")
    val remarks: String? = null
)