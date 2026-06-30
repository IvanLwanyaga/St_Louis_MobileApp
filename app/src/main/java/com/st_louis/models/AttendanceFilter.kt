package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class AttendanceFilter(
    @SerializedName("class_id")
    val classId: String? = null,

    @SerializedName("class_name")
    val className: String? = null,

    @SerializedName("section")
    val section: String? = null,

    @SerializedName("date_from")
    val dateFrom: String? = null,

    @SerializedName("date_to")
    val dateTo: String? = null,

    @SerializedName("student_id")
    val studentId: String? = null,

    @SerializedName("status")
    val status: String? = null // "present", "absent", "late", "excused"
)