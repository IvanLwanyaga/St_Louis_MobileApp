package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: UserRole,

    @SerializedName("staff_id")
    val staffId: String? = null,

    @SerializedName("department")
    val department: String? = null,

    @SerializedName("avatar")
    val avatar: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("date_of_birth")
    val dateOfBirth: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("qualification")
    val qualification: String? = null,

    @SerializedName("years_of_experience")
    val yearsOfExperience: Int? = null,

    @SerializedName("subjects")
    val subjects: List<String>? = null,

    @SerializedName("classes_assigned")
    val classesAssigned: List<String>? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)