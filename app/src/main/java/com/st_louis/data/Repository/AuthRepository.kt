package com.st_louis.data.repository

import com.st_louis.data.ApiService
import com.st_louis.models.LoginRequest
import com.st_louis.models.LoginResponse
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }
}