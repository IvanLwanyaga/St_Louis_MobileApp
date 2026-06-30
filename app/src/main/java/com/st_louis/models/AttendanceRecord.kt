package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class AttendanceRecord(
    @SerializedName("student_id")
    val studentId: String,

    @SerializedName("student_name")
    val studentName: String? = null,

    @SerializedName("admission_no")
    val admissionNo: String? = null,

    @SerializedName("present")
    val present: Boolean,

    @SerializedName("status")
    val status: String? = null, // "present", "absent", "late", "excused"

    @SerializedName("remarks")
    val remarks: String? = null,

    @SerializedName("time_in")
    val timeIn: String? = null,

    @SerializedName("time_out")
    val timeOut: String? = null
)