package com.st_louis.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.st_louis.data.ApiService

class ScheduleViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}