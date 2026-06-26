package com.st_louis.models

data class Student(
    // Remove @DocumentId annotation
    val id: String = "",

    // Personal Information
    val name: String = "",
    val admissionNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",

    // Academic Information
    val className: String = "",
    val stream: String = "",
    val level: String = "PRIMARY", // PRE_PRIMARY or PRIMARY
    val stage: String = "",

    // Pre-Primary Specific
    val playgroup: String = "",
    val ageGroup: String = "",
    val developmentalMilestones: String = "",
    val toiletTraining: Boolean = false,
    val napSchedule: String = "",

    // Primary Specific
    val house: String = "",
    val coCurricular: List<String> = emptyList(),
    val subjects: List<String> = emptyList(),

    // Parent/Guardian Information
    val parentName: String = "",
    val parentEmail: String = "",
    val parentPhone: String = "",
    val alternativeEmail: String = "",
    val emergencyContact: String = "",

    // Medical Information
    val medicalConditions: String = "",
    val allergies: String = "",

    // Transport Information
    val busRoute: String = "",
    val pickUpPoint: String = "",

    // System Credentials
    val username: String = "",
    val temporaryPassword: String = "",
    val role: String = "student",

    // Enrollment Information
    val enrollmentDate: String = "",
    val academicYear: String = "",
    val enrollmentStatus: String = "active", // active, graduated, transferred, withdrawn

    // Fees Information
    val feesStatus: String = "pending",
    val feesBalance: Double = 0.0,

    // Metadata
    val createdBy: String = "",
    val updatedBy: String = "",

    // Changed from Date to String since Laravel returns strings
    val createdAt: String? = null,
    val updatedAt: String? = null
)