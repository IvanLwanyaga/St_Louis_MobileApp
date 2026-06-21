package com.st_louis.models

data class UserAccount(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val userId: String = "", // Admission No or Staff ID
    val role: String = "",   // STUDENT, TEACHER, BURSAR, PARENT
    val className: String = "",
    val stream: String = "",
    val parentEmail: String = "",
    val generatedUsername: String = "",
    val tempPassword: String = "",
    val status: String = "ACTIVE"
)
