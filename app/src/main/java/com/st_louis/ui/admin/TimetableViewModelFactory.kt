package com.st_louis.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.st_louis.data.ApiService
import com.st_louis.ui.admin.timetable.TimetableViewModel

class TimetableViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimetableViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}