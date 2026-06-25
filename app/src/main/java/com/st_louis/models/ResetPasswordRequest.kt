package com.st_louis.models

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)